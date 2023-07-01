package com.chenc.fuploads.service

import com.chenc.fuploads.pojo.UploadStatus
import com.chenc.fuploads.utils.FTPUtils
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.pool2.impl.GenericObjectPool
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FTPService(@Autowired var pool: GenericObjectPool<FTPClient>) {

    val log: Logger = LoggerFactory.getLogger(FTPService::class.java)

    fun upload(
            file: ByteArray?,
            path: String,
            fileName: String,
            mkDir: Boolean = false
    ): UploadStatus {
        var result: UploadStatus
        var ftpClient: FTPClient? = null
        try {
            ftpClient = pool.borrowObject()
            if (!FTPUtils.isExist(ftpClient, path) && mkDir && !FTPUtils.mkDir(ftpClient, path)) {
                result = UploadStatus.DIRECTORY_NOT_EXIST
                log.warn("${path} DIRECTORY_NOT_EXIST")
                return result
            }
            val changeRes = ftpClient.changeWorkingDirectory(path)
            log.info("changeWorkingDirectory: ${path} ,status: ${changeRes}")
            result = FTPUtils.upload(ftpClient, file, fileName)
            log.info("write file: ${fileName} done, path: ${path}")
        } catch (e: Exception) {
            log.error("upload error", e)
            result = UploadStatus.UNKNOWN_ERROR
        } finally {
            ftpClient?.let { pool.returnObject(ftpClient) }
        }
        return result
    }

    fun mkDir(path: String) {
        var ftpClient: FTPClient? = null
        try {
            ftpClient = pool.borrowObject()
            val res = FTPUtils.mkDir(ftpClient, path)
            log.info("mkDir ${path},  status: ${res}")
        } catch (e: Exception) {
            log.error("mkDir", e)
        } finally {
            ftpClient?.let { pool.returnObject(ftpClient) }
        }
    }
}
