package com.chenc.fuploads.apis

import com.chenc.fuploads.pojo.BaseResponse
import com.chenc.fuploads.pojo.UploadFileResponse
import com.chenc.fuploads.pojo.UploadStatus
import com.chenc.fuploads.service.ArchiveService
import com.chenc.fuploads.service.FTPService
import com.chenc.fuploads.apis.helper.FileUploadHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/** 文件上传 Controller /api/ */
@RequestMapping("/api")
@RestController
class FileUploadController {

    val log: Logger = LoggerFactory.getLogger(FileUploadController::class.java)

    @Autowired lateinit var ftpService: FTPService
    @Autowired lateinit var archiveService: ArchiveService

    /** 文件上传接口 /api/uploads */
    @RequestMapping("/uploads", method = arrayOf(RequestMethod.POST))
    fun uploads(
            @RequestParam("file") files: Array<MultipartFile>,
            @RequestParam("path", required = false, defaultValue = "/test/tmp/") path: String
    ): BaseResponse<UploadFileResponse> {
        log.info("uploads file count: ${files.size}")
        
        var successFileList = ArrayList<String>()
        var failedFileList = ArrayList<String>()

        var result: UploadStatus =
                FileUploadHelper.uploads_and_extract(
                        ftpService,
                        archiveService,
                        files,
                        path,
                        successFileList,
                        failedFileList
                )

        return BaseResponse.build {
            code = result.code
            message = result.message
            data = UploadFileResponse(successFileList, failedFileList)
        }
    }
}
