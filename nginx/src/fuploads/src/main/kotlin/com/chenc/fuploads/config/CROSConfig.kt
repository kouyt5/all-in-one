package com.chenc.fuploads.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CROSConfig {

    @Bean
    fun corsFilter() : CorsFilter {

        val config = CorsConfiguration()
        
        //开放哪些ip、端口、域名的访问权限，星号表示开放所有域
        config.addAllowedOriginPattern("*")
        //是否允许发送Cookie信息
        config.setAllowCredentials(true)
        //开放哪些Http方法，允许跨域访问
        config.addAllowedMethod("GET")
        config.addAllowedMethod("POST")
        config.addAllowedMethod("PUT")
        config.addAllowedMethod("DELETE")
        config.addAllowedMethod("OPTIONS")

        //允许HTTP请求中的携带哪些Header信息
        config.addAllowedHeader("*")
        //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        config.addExposedHeader("*")

        //添加映射路径，“/**”表示对所有的路径实行全局跨域访问权限的设置
        val configSource = UrlBasedCorsConfigurationSource()
        configSource.registerCorsConfiguration("/**", config)
        return CorsFilter(configSource)
    }
}