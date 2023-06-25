package com.chenc.fuploads.pojo


data class UploadFileResponse (
    var successFileList: List<String>?,
    var failedFileList: List<String>?,
    ) {
}