package com.chenc.fuploads.utils

import java.io.InputStream
import java.io.IOException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.ArchiveException

class ExtractFileUtils {

    companion object {
        val log: Logger = LoggerFactory.getLogger(ExtractFileUtils::class.java)

        @Throws(IOException::class)
        fun extract(
                compressedTarget: ArchiveInputStream,
                callback: (file: ByteArray, path: String, isDictory: Boolean) -> Unit
        ) {
            var entry: ArchiveEntry? = null
            val i = ArchiveStreamFactory().createArchiveInputStream(compressedTarget)
            entry = i.getNextEntry()
            while (entry != null) {
                if (!i.canReadEntryData(entry)) {
                    log.error("cannot ReadEntryData")
                }
                entry = i.getNextEntry()
            }
        }
    }
}
