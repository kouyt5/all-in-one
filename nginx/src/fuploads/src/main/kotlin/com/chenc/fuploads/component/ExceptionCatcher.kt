package com.chenc.fuploads.component

import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MultipartException
import  org.springframework.web.multipart.support.MissingServletRequestPartException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.chenc.fuploads.pojo.BaseResponse
import com.chenc.fuploads.pojo.ResponseStatus

@RestControllerAdvice
class ExceptionCatcher() {

    val log: Logger = LoggerFactory.getLogger(ExceptionCatcher::class.java)

    @ExceptionHandler(MultipartException::class)
    fun multipartException(e: MultipartException): BaseResponse<Any?> {
        log.error("multipartException: ", e)
        return BaseResponse.build {
            code = ResponseStatus.FILE_READ_ERROR.code
            message = ResponseStatus.FILE_READ_ERROR.message
            data = null
        }
    }

    @ExceptionHandler(MissingServletRequestPartException::class)
    fun missingReqPartException(e: MissingServletRequestPartException): BaseResponse<Any?> {
        log.error("missingReqPartException: ", e)
        return BaseResponse.build {
            code = ResponseStatus.FILE_MISS_ERROR.code
            message = ResponseStatus.FILE_MISS_ERROR.message
            data = null
        }
    }

    @ExceptionHandler(Exception::class)
    fun exception(e: Exception): BaseResponse<Any?> {
        log.error("exception: ", e)
        return BaseResponse.build {
            code = ResponseStatus.UNKNOWN_ERROR.code
            message = ResponseStatus.UNKNOWN_ERROR.message
            data = null
        }
    }

}