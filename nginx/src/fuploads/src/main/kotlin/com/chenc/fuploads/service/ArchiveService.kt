package com.chenc.fuploads.service

import com.chenc.fuploads.pojo.UploadStatus
import java.io.ByteArrayInputStream
import kotlin.io.path.Path
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 压缩文件 Service
 * @author kouyt5
 */
@Service
class ArchiveService {

    val log: Logger = LoggerFactory.getLogger(ArchiveService::class.java)

    /**
     * 压缩文件提取
     */
    fun extract(
            compressedFile: ByteArray,
            path: String,
            callback: (file: ByteArray?, path: String, isDictory: Boolean) -> UploadStatus
    ): UploadStatus {
        var result: UploadStatus = UploadStatus.SUCCESS
        var byteInputStream = ByteArrayInputStream(compressedFile)
        val i = ArchiveStreamFactory().createArchiveInputStream(byteInputStream)
        var entry: ArchiveEntry? = i.getNextEntry()
        while (entry != null) {
            if (!i.canReadEntryData(entry)) {
                log.error("cannot ReadEntryData")
                result = UploadStatus.EXTRACT_ERROR
                return result
            }
            val fileRelPath: String = Path(path, entry.name).toString()
            if (entry.isDirectory) {
                result = callback(null, fileRelPath, true)
            } else {
                val file: ByteArray = i.readAllBytes()
                result = callback(file, fileRelPath, false)
            }
            if (result != UploadStatus.SUCCESS) {
                log.warn("extract to ftp error: path=${fileRelPath}")
                return result
            }
            entry = i.getNextEntry()
        }
        return result
    }
}
