package com.chenc.fuploads.utils

import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import java.io.OutputStream
import java.io.BufferedOutputStream
import java.io.IOException
import com.chenc.fuploads.pojo.UploadStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class FTPUtils {
    companion object {
        val log: Logger = LoggerFactory.getLogger(FTPUtils::class.java)
        var LOCAL_CHARSET: String = "UTF-8"
        var SERVER_CHARSET: String = "ISO-8859-1"

        fun upload(client: FTPClient, file: ByteArray?, fileName: String): UploadStatus {
            var result: UploadStatus = UploadStatus.SUCCESS
            try {
                var out = client.appendFileStream(fileName)
                if (out == null) {
                    log.info("client OutputStream = null")
                    throw Exception()
                }
                out.write(file)
                out.flush()
                out.close()
                var complete: Boolean = client.completePendingCommand()
                if (!complete) {
                    result = UploadStatus.UNKNOWN_ERROR
                    log.error("upload not complete")
                }
            } catch (e: IOException) {
                log.error("upload IOException", e)
                result = UploadStatus.UNKNOWN_ERROR
            } catch (e: Exception) {
                log.error("upload Exception", e)
                result = UploadStatus.UNKNOWN_ERROR
            }
            
            return result
        }

        fun isExist(client: FTPClient, path: String) : Boolean {
            var result = true
            if (client.listFiles(path) == null) {
                result = false
            }
            result = client.changeWorkingDirectory(path)
            if (!result) {
                result = false
            }
            log.info("${path} is Exist ${result}")
            return result
        }

        fun mkDir(client: FTPClient, path: String) : Boolean {
            log.info("create path ${path}")
            return client.makeDirectory(path)
        }
        
    }
}