package com.chenc.fuploads.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.apache.commons.net.ftp.FTPClient
import com.chenc.fuploads.component.FTPFactory

/**
 * https://huaweicloud.csdn.net/6387686adacf622b8df8b4f3.html
 */
@EnableConfigurationProperties
@Configuration
class FTPConfig: GenericObjectPoolConfig<FTPClient>() {

    @Value("\${ftp.host}") lateinit var host: String
    @Value("\${ftp.port}") var port: Int = 21
    @Value("\${ftp.username}") lateinit var username: String
    @Value("\${ftp.password}") lateinit var password: String
    @Value("\${ftp.passiveMode}") var passiveMode: Boolean = true
    @Value("\${ftp.defaultPath}") lateinit var defaultPath: String
    @Value("\${ftp.timeout}") var timeout: Int = 30000 
    @Value("\${ftp.buffeSize}") var bufferSize: Int = 8192
    @Value("\${ftp.minldle}") var minldle: Int = 10
    @Value("\${ftp.maxldle}")  var maxldle: Int = 20
    @Value("\${ftp.maxWait}") var maxWait: Int = 30000
    @Value("\${ftp.block}")  var block: Boolean = true

    @Bean
    fun createPool(@Autowired factory: FTPFactory): GenericObjectPool<FTPClient> {
        return GenericObjectPool<FTPClient>(factory, this@FTPConfig)
    }
}
