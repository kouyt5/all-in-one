package com.chenc.fuploads

import com.chenc.fuploads.pojo.UploadStatus
import com.chenc.fuploads.utils.FTPUtils
import java.nio.charset.Charset
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.pool2.impl.GenericObjectPool
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FTPService(@Autowired var pool: GenericObjectPool<FTPClient>) {

    val log: Logger = LoggerFactory.getLogger(FTPService::class.java)

    fun upload(file: ByteArray, path: String, fileName: String): UploadStatus {
        var result: UploadStatus
        var ftpClient: FTPClient? = null
        try {
            ftpClient = pool.borrowObject()
            if (!FTPUtils.isExist(ftpClient, path)) {
                result = UploadStatus.DIRECTORY_NOT_EXIST
                return result
            }
            ftpClient.changeWorkingDirectory(path)
            // https://blog.csdn.net/gingerredjade/article/details/62036205
            // var encodeFileName: String =
            //         String(
            //                 fileName.toByteArray(Charset.forName(FTPUtils.LOCAL_CHARSET)),
            //                 Charset.forName(FTPUtils.SERVER_CHARSET)
            // )
            log.info("before upload job have done!")
            result = FTPUtils.upload(ftpClient, file, fileName)
            log.info("write file: ${fileName} done")
        } catch (e: Exception) {
            log.error("upload error", e)
            result = UploadStatus.UNKNOWN_ERROR
        } finally {
            ftpClient?.let { pool.returnObject(ftpClient) }
        }
        return result
    }
}
