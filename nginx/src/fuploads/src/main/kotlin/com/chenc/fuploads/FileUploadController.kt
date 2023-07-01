package com.chenc.fuploads

import com.chenc.fuploads.pojo.BaseResponse
import com.chenc.fuploads.pojo.UploadFileResponse
import com.chenc.fuploads.pojo.UploadStatus
import com.chenc.fuploads.service.ArchiveService
import com.chenc.fuploads.service.FTPService
import kotlin.io.path.Path
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/** 文件上传 Controller /api/  */
@RequestMapping("/api")
@RestController
class FileUploadController {

    val log: Logger = LoggerFactory.getLogger(FileUploadController::class.java)

    @Autowired lateinit var ftpService: FTPService
    @Autowired lateinit var archiveService: ArchiveService

    /**
     * 文件上传接口 /api/uploads
     */
    @RequestMapping("/uploads", method = arrayOf(RequestMethod.POST))
    fun uploads(
            @RequestParam("file") files: Array<MultipartFile>,
            @RequestParam("path", required = false, defaultValue = "/test/tmp/") path: String
    ): BaseResponse<UploadFileResponse> {
        log.info("uploads file count: ${files.size}")
        var result: UploadStatus = UploadStatus.SUCCESS
        var successFileList = ArrayList<String>()
        var failedFileList = ArrayList<String>()

        var time: Long = System.currentTimeMillis()
        for (file in files) {
            val fileName = file.originalFilename ?: "unknown"
            val status = ftpService.upload(file.bytes, path, fileName)
            if (status == UploadStatus.SUCCESS) {
                successFileList.add(fileName)
                extractFile(file, path)
            } else {
                failedFileList.add(fileName)
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

    private fun supportExtractFormat(s: String): Boolean {
        return Regex(""".*zip$|.*tar$|.*7z$""").matches(s)
    }

    private fun extractFile(file: MultipartFile, path: String): UploadStatus {
        var resExt: UploadStatus = UploadStatus.EXTRACT_NO_NEED
        if (!supportExtractFormat(file.originalFilename ?: "_")) {
            return resExt
        }
        val extractTo =
                Path(path, file.originalFilename ?: "_")
                        .toString()
                        .replace(Regex("""\.zip$|\.tar$|\.7z$"""), "")
        log.info("extractTo: ${extractTo}")
        ftpService.mkDir(extractTo)
        resExt =
                archiveService.extract(file.bytes, extractTo) { fileByte, relPath, isDictory ->
                    var res = UploadStatus.SUCCESS
                    if (isDictory) {
                        ftpService.mkDir(relPath)
                    } else {
                        val fileName = Path(relPath).fileName.toString()
                        val dirPath = Path(relPath).parent.toString()
                        res = ftpService.upload(fileByte, dirPath, fileName, true)
                    }
                    res
                }
        log.info("extract res: ${resExt.message}")
        return resExt
    }
}
