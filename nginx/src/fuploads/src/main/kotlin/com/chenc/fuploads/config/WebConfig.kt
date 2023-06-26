package com.chenc.fuploads.config

import com.chenc.fuploads.component.RequestUrlInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    @Autowired lateinit var requestUrlInterceptor: RequestUrlInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(requestUrlInterceptor).addPathPatterns("/api/**")
    }
}
