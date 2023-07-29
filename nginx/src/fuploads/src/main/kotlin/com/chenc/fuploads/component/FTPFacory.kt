package com.chenc.fuploads.component

import com.chenc.fuploads.config.FTPConfig
import com.chenc.fuploads.utils.FTPUtils
import java.io.IOException
import java.net.InetAddress
import org.apache.commons.lang3.StringUtils
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.PooledObjectFactory
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * FTPClient 对象池
 */
@Component
class FTPFactory : PooledObjectFactory<FTPClient> {

    @Autowired lateinit var config: FTPConfig
    val log: Logger = LoggerFactory.getLogger(FTPFactory::class.java)

    override fun activateObject(p0: PooledObject<FTPClient>) {
        val ftpClient = p0.getObject()
        try {
            ftpClient.connect(config.host, config.port)
            ftpClient.login(config.username, config.password)
            ftpClient.controlEncoding = FTPUtils.LOCAL_CHARSET
            // ftpClient.changeWorkingDirectory(config.defaultPath) // IOExcep
            // 设置上传文件类型为二进制，否则将无法打开文件
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE) // IOExcep
        } catch (e: IOException) {
            log.error("activateObject error", e)
        }
    }

    override fun makeObject(): PooledObject<FTPClient>? {
        log.info("开始创建ftp连接")
        val ftpClient: FTPClient = FTPClient()
        try {
            ftpClient.connectTimeout = config.timeout
            ftpClient.connect(config.host, config.port)
            var reply = ftpClient.replyCode
            if (!FTPReply.isPositiveCompletion(reply)) {
                log.warn("client disconnect")
                ftpClient.disconnect()
            }
            if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))
            ) { // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                FTPUtils.LOCAL_CHARSET = "UTF-8"
                log.info("开启服务器对UTF-8的支持")
            } else {
                FTPUtils.LOCAL_CHARSET = "GBK"
                log.info("开启服务器对GBK的支持")
            }
            // 支持匿名登陆
            var loginStatus: Boolean
            if (StringUtils.isBlank(config.username)) {
                loginStatus = ftpClient.login("anonymous", "anonymous")
            } else {
                loginStatus = ftpClient.login(config.username, config.password)
            }
            if (!loginStatus) {
                log.warn("ftp登陆失败")
            }
            if (config.passiveMode) {
                ftpClient.enterLocalPassiveMode()
            }
            // 0=ASCII_FILE_TYPE(ASCII格式)，1=EBCDIC_FILE_TYPE，2=LOCAL_FILE_TYPE(二进制文件)
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
            ftpClient.bufferSize = config.bufferSize
            ftpClient.controlEncoding = FTPUtils.LOCAL_CHARSET
            
        } catch (e: IOException) {
            log.error("makeObject failed: ", e)
        }
        return DefaultPooledObject<FTPClient>(ftpClient)
    }

    override fun passivateObject(p0: PooledObject<FTPClient>) {
        val ftpClient = p0.getObject()
        try {
            // ftpClient.changeWorkingDirectory(config.defaultPath)
            ftpClient.logout()
            if (ftpClient.isConnected) {
                ftpClient.disconnect()
            }
        } catch (e: Exception) {
            log.debug("couldnt disconnect from server: ", e)
        }
    }

    override fun validateObject(p0: PooledObject<FTPClient>): Boolean {
        val ftpClient = p0.getObject()
        try {
            return ftpClient.sendNoOp()
        } catch (e: Exception) {
            log.debug("验证ftp object failed: ", e)
            return false
        }
    }

    override fun destroyObject(p0: PooledObject<FTPClient>) {
        val ftpClient = p0.getObject()
        try {
            ftpClient.disconnect()
        } catch (e: Exception) {
            log.debug("destory ftp object failed: ", e)
        }
    }
}
