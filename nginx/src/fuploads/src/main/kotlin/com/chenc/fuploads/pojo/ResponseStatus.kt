package com.chenc.fuploads.pojo

enum class ResponseStatus(val code: Int, val message: String) {
    SUCCESS(200, "OK"),

    FILE_READ_ERROR(1001, "文件读取错误, 请检查文件是否损坏或参数是否正确"),
    FILE_MISS_ERROR(1002, "缺少文件参数内容，请检查请求"),
    FILE_MISS_PARAM_ERROR(1003, "缺少文件参数"),
    UNKNOWN_ERROR(1100, "未知错误"),
}