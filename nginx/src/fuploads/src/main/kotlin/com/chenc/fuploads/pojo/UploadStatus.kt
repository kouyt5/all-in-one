package com.chenc.fuploads.pojo

enum class UploadStatus(val code: Int, val message: String) {
    SUCCESS(200, "OK"),
    UNKNOWN_ERROR(500, "未知错误"),
    DIRECTORY_NOT_EXIST(400, "目录不存在"),
    MULTIFILE_ERROR(401, "多文件上传错误")
}
