package com.chenc.fuploads.component

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RequestUrlInterceptor : HandlerInterceptor {

    val log: Logger = LoggerFactory.getLogger(FTPFactory::class.java)

    override fun preHandle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            handler: Any
    ): Boolean {
        
        var ip: String? = getRealIP(request)
        log.info("curr ip: ${ip}. req_url: ${request.requestURL}. method: ${request.method}")
        return true
    }

    private fun ipValid(ip: String?): Boolean {
        return ip != null && ip.length != 0 && "unknown" != ip
    }

    private fun getRealIP(request: HttpServletRequest): String? {
        var ip: String? = request.getHeader("x-forwarded-for")

        if (ipValid(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip?.indexOf(",") != -1) {
                ip = ip!!.split(",")[0]
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
        return ip
    }
}
