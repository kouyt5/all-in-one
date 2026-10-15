package com.chenc.fuploads.component

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.support.MissingServletRequestPartException

@RestControllerAdvice
class ExceptionCatcher {
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun tooLarge(e: MaxUploadSizeExceededException) = ResponseEntity.status(413).body(mapOf("code" to 413, "message" to "文件超过上传限制"))
    @ExceptionHandler(IllegalArgumentException::class, MissingServletRequestPartException::class, MultipartException::class)
    fun invalid(e: Exception) = ResponseEntity.badRequest().body(mapOf("code" to 400, "message" to "请求参数无效"))
}
