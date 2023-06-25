package com.chenc.fuploads

import com.chenc.fuploads.pojo.UploadStatus
import com.chenc.fuploads.pojo.BaseResponse
import com.chenc.fuploads.pojo.UploadFileResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile

/**
 * 文件上传接口
 * /api/uploads
 */
@RequestMapping("/api")
@RestController
class FileUploadController {

    val log: Logger = LoggerFactory.getLogger(FileUploadController::class.java)

    @Autowired lateinit var ftpService: FTPService

    @RequestMapping("/uploads", method=arrayOf(RequestMethod.POST))
    fun uploads(
            @RequestParam("file") files: Array<MultipartFile>,
            @RequestParam("path", required=false, defaultValue="/test/tmp/") path: String
    ): BaseResponse<UploadFileResponse> {
        log.info("uploads file count: ${files.size}")
        var result : UploadStatus = UploadStatus.SUCCESS
        var successFileList = ArrayList<String>()
        var failedFileList = ArrayList<String>()

        var time: Long = System.currentTimeMillis()
        for (file in files) {
            var status = ftpService.upload(file.bytes, path, file.originalFilename?:"unknown")
            if (status == UploadStatus.SUCCESS) {
                successFileList.add(file.originalFilename?:"unknown")
            } else {
                failedFileList.add(file.originalFilename?:"unknown")
            }
            result = status
        }
        log.info("上传耗时: ${System.currentTimeMillis() - time} ms")
        if (files.size > 1) {
            if (failedFileList.size > 0) {
                result = UploadStatus.MULTIFILE_ERROR
            }
        }
        return BaseResponse.build {
            code = result.code
            message = result.message
            data = UploadFileResponse(successFileList, failedFileList)
        }
    }
}
