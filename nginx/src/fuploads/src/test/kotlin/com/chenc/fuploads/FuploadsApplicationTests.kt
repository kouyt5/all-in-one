package com.chenc.fuploads

import com.chenc.fuploads.service.StorageService
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.mock.web.MockMultipartFile

class FuploadsApplicationTests {
    @TempDir lateinit var directory: Path
    private fun file(name: String, bytes: ByteArray = "hello".toByteArray()) = MockMultipartFile("file", name, "application/octet-stream", bytes)
    private fun zip(name: String, content: ByteArray = "content".toByteArray()): ByteArray {
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { it.putNextEntry(ZipEntry(name)); it.write(content); it.closeEntry() }
        return out.toByteArray()
    }
    @Test fun `uploads nested path and prevents overwrite`() {
        val service = StorageService(directory.toString())
        service.upload(file("hello.txt"), "nested")
        assertEquals("hello", Files.readString(directory.resolve("nested/hello.txt")))
        assertThrows(IllegalArgumentException::class.java) { service.upload(file("hello.txt"), "nested") }
    }
    @Test fun `rejects traversal and symlink destination`() {
        val service = StorageService(directory.toString())
        for (name in listOf("../escape", ".secret", "a/b", "a\\b")) assertThrows(IllegalArgumentException::class.java) { service.upload(file(name), "/") }
        assertThrows(IllegalArgumentException::class.java) { service.upload(file("safe"), "../outside") }
        Files.createSymbolicLink(directory.resolve("link"), directory.parent)
        assertThrows(IllegalArgumentException::class.java) { service.upload(file("safe"), "link") }
    }
    @Test fun `extracts zip and rolls back unsafe archives`() {
        val service = StorageService(directory.toString())
        service.upload(file("good.zip", zip("nested/a.txt")), "/")
        assertEquals("content", Files.readString(directory.resolve("good/nested/a.txt")))
        assertThrows(IllegalArgumentException::class.java) { service.upload(file("bad.zip", zip("../escape")), "/") }
        assertFalse(Files.exists(directory.resolve("bad.zip")))
        assertFalse(Files.exists(directory.resolve("bad")))
        Files.list(directory.resolve(".tmp")).use { assertEquals(0, it.count()) }
    }
    @Test fun `rejects ZIP symlinks encoded in the central directory`() {
        val service = StorageService(directory.toString())
        val bytes = zip("link", "../outside".toByteArray())
        val buffer = ByteBuffer.wrap(bytes).order(LITTLE_ENDIAN)
        val central = (0 until bytes.size - 46).first { buffer.getInt(it) == 0x02014b50 }
        buffer.putShort(central + 4, 0x0314.toShort())
        buffer.putInt(central + 38, 0xa1ff shl 16)
        assertThrows(IllegalArgumentException::class.java) { service.upload(file("link.zip", bytes), "/") }
        assertFalse(Files.exists(directory.resolve("link.zip")))
    }
    @Test fun `rejects zip bomb and empty file`() {
        val service = StorageService(directory.toString())
        assertThrows(IllegalArgumentException::class.java) { service.upload(file("empty", byteArrayOf()), "/") }
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { zip ->
            for (i in 1..2001) { zip.putNextEntry(ZipEntry("$i.txt")); zip.write(1); zip.closeEntry() }
        }
        assertThrows(IllegalArgumentException::class.java) { service.upload(file("many.zip", out.toByteArray()), "/") }
    }
}
