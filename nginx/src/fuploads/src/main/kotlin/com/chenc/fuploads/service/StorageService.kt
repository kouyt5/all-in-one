package com.chenc.fuploads.service

import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardOpenOption.CREATE_NEW
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipFile
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class StorageService(@Value("\${upload.root}") directory: String) {
    val root: Path = Path.of(directory).toAbsolutePath().normalize()
    init { Files.createDirectories(root.resolve(".tmp")) }

    private fun target(base: Path, name: String): Path {
        require(!name.contains('\\') && !name.any { it.isISOControl() }) { "非法路径" }
        val relative = Path.of(name)
        require(!relative.isAbsolute && relative.none { it.toString().startsWith(".") }) { "非法路径" }
        val result = base.resolve(relative).normalize()
        require(result.startsWith(base) && result != base) { "非法路径" }
        var check: Path? = result
        while (check != null && check.startsWith(root)) {
            require(!Files.isSymbolicLink(check)) { "不允许符号链接" }
            check = check.parent
        }
        return result
    }

    private fun copyLimited(input: InputStream, output: Path, limit: Long): Long {
        var count = 0L
        Files.newOutputStream(output, CREATE_NEW).use { out ->
            val buffer = ByteArray(8192)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                count += read
                require(count <= limit) { "文件或解压内容超过限制" }
                out.write(buffer, 0, read)
            }
        }
        return count
    }

    @Synchronized
    fun upload(file: MultipartFile, path: String): String {
        val filename = file.originalFilename ?: throw IllegalArgumentException("缺少文件名")
        require(filename.isNotBlank() && !filename.contains('/') && !filename.contains('\\')) { "非法文件名" }
        require(!file.isEmpty && file.size <= 100L * 1024 * 1024) { "文件为空或超过 100 MB" }
        val destination = if (path == "/" || path.isBlank()) root else target(root, path.removePrefix("/"))
        val output = target(destination, filename)
        require(!Files.exists(output, NOFOLLOW_LINKS)) { "同名文件已存在" }
        require(Files.getFileStore(root).usableSpace > file.size + 512L * 1024 * 1024) { "磁盘空间不足" }
        val stage = Files.createTempDirectory(root.resolve(".tmp"), "upload-")
        val committed = mutableListOf<Path>()
        try {
            val stagedFile = stage.resolve(filename)
            file.inputStream.use { copyLimited(it, stagedFile, 100L * 1024 * 1024) }
            val extension = filename.substringAfterLast('.', "").lowercase()
            if (extension == "zip") validateZip(stagedFile)
            val extracted = if (extension in setOf("zip", "tar")) stage.resolve(filename.substringBeforeLast('.')) else null
            if (extracted != null) {
                target(destination, extracted.fileName.toString()).also { require(!Files.exists(it, NOFOLLOW_LINKS)) { "解压目录已存在" } }
                Files.createDirectory(extracted)
                var total = 0L
                var entries = 0
                Files.newInputStream(stagedFile).use { raw ->
                    val archiveStream: ArchiveInputStream<out ArchiveEntry> =
                        if (extension == "zip") ZipArchiveInputStream(raw)
                        else TarArchiveInputStream(raw)
                    archiveStream.use { archive ->
                        while (true) {
                            val entry = archive.nextEntry ?: break
                            require(++entries <= 2000 && archive.canReadEntryData(entry)) { "压缩包条目无效或过多" }
                            require(!(entry is TarArchiveEntry && (entry.isSymbolicLink || entry.isLink || (!entry.isDirectory && !entry.isFile)))) { "不允许压缩包链接或特殊文件" }
                            require(!(entry is ZipArchiveEntry && entry.isUnixSymlink)) { "不允许压缩包符号链接" }
                            val entryName = entry.name.removePrefix("./").trimEnd('/')
                            if (entry.isDirectory && entryName in setOf("", ".")) continue
                            val dest = target(extracted, entryName)
                            if (entry.isDirectory) Files.createDirectories(dest) else {
                                Files.createDirectories(dest.parent)
                                total += copyLimited(archive, dest, 256L * 1024 * 1024 - total)
                            }
                        }
                    }
                }
            }
            Files.createDirectories(destination)
            Files.move(stagedFile, output, ATOMIC_MOVE)
            committed.add(output)
            if (extracted != null) {
                val dest = destination.resolve(extracted.fileName)
                Files.move(extracted, dest, ATOMIC_MOVE)
                committed.add(dest)
            }
            return filename
        } catch (e: Exception) {
            committed.forEach { deleteTree(it) }
            throw e
        } finally { deleteTree(stage) }
    }

    private fun validateZip(path: Path) {
        // Bound central-directory memory before opening the random-access ZIP reader.
        RandomAccessFile(path.toFile(), "r").use { file ->
            val length = file.length()
            val tail = ByteArray(minOf(length, 65557L).toInt())
            file.seek(length - tail.size)
            file.readFully(tail)
            val bytes = ByteBuffer.wrap(tail).order(LITTLE_ENDIAN)
            val end = (tail.size - 22 downTo 0).firstOrNull { i ->
                bytes.getInt(i) == 0x06054b50 &&
                    i + 22 + (bytes.getShort(i + 20).toInt() and 0xffff) == tail.size
            } ?: throw IllegalArgumentException("ZIP 中央目录无效")
            val count = bytes.getShort(end + 10).toInt() and 0xffff
            val size = bytes.getInt(end + 12).toLong() and 0xffffffffL
            val offset = bytes.getInt(end + 16).toLong() and 0xffffffffL
            require(bytes.getShort(end + 4).toInt() == 0 && bytes.getShort(end + 6).toInt() == 0 &&
                (bytes.getShort(end + 8).toInt() and 0xffff) == count && count <= 2000 &&
                size <= 1024 * 1024 && offset + size <= length - tail.size + end) {
                "不支持分卷、ZIP64、过多条目或超大 ZIP 中央目录"
            }
        }
        ZipFile.builder().setPath(path).get().use { zip ->
            val entries = zip.entries
            var count = 0
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                require(++count <= 2000 && !entry.isUnixSymlink && zip.canReadEntryData(entry)) {
                    "ZIP 条目不安全或不受支持"
                }
            }
        }
    }

    private fun deleteTree(path: Path) {
        if (Files.exists(path, NOFOLLOW_LINKS)) Files.walk(path).use { paths -> paths.sorted(Comparator.reverseOrder()).forEach { Files.deleteIfExists(it) } }
    }
}
