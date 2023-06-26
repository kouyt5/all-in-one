package com.chenc.fuploads.component

import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.stereotype.Component
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Component
class RequestUrlInterceptor: HandlerInterceptor {

    val log: Logger = LoggerFactory.getLogger(FTPFactory::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        var ip: String? = request.getHeader("x-forwarded-for")

        if (ipValid(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if( ip?.indexOf(",") != -1 ){
                ip = ip!!.split(",")[0];
            }
        }
        if (!ipValid(ip)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (!ipValid(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (!ipValid(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (!ipValid(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (!ipValid(ip)) {
            ip = request.getHeader("X-Real-IP")
        }
        if (!ipValid(ip)) {
            ip = request.getRemoteAddr()
        }
        log.info("curr ip: ${ip}")
        return true;
     }

     private fun ipValid(ip: String?) : Boolean {
        return ip != null && ip.length != 0 && "unknown" != ip
     }
}