package com.chenc.fuploads.apis.helper

import com.chenc.fuploads.pojo.UploadStatus
import com.chenc.fuploads.service.ArchiveService
import com.chenc.fuploads.service.FTPService
import kotlin.io.path.Path
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.multipart.MultipartFile

/**
 * FileUploadController 辅助类
 */
class FileUploadHelper {
    companion object {

        val log: Logger = LoggerFactory.getLogger(FileUploadHelper::class.java)

        /**
         * 上传文件并提取压缩文件
         */
        fun uploads_and_extract(
                ftpService: FTPService,
                archiveService: ArchiveService,
                files: Array<MultipartFile>,
                path: String,
                successFileList: ArrayList<String>,
                failedFileList: ArrayList<String>,
        ): UploadStatus {
            var result: UploadStatus = UploadStatus.SUCCESS
            for (file in files) {
                val fileName = file.originalFilename ?: "unknown"
                var time: Long = System.currentTimeMillis()
                val status = ftpService.upload(file.bytes, path, fileName)
                log.info("上传: ${fileName} 耗时: ${System.currentTimeMillis() - time} ms")
                if (status == UploadStatus.SUCCESS) {
                    successFileList.add(fileName)
                    extractFile(file, path, ftpService, archiveService)
                } else {
                    failedFileList.add(fileName)
                }
                result = status
            }
            
            if (files.size > 1) {
                if (failedFileList.size > 0) {
                    result = UploadStatus.MULTIFILE_ERROR
                }
            }
            return result
        }

        private fun extractFile(
                file: MultipartFile,
                path: String,
                ftpService: FTPService,
                archiveService: ArchiveService
        ): UploadStatus {
            var resExt: UploadStatus = UploadStatus.EXTRACT_NO_NEED
            val filename = file.originalFilename ?: "_"
            if (!supportExtractFormat(filename)) {
                return resExt
            }
            val extractTo =
                    Path(path, filename)
                            .toString()
                            .replace(Regex("""\.zip$|\.tar$|\.7z$"""), "")
            log.info("extractTo: ${extractTo}")
            var time: Long = System.currentTimeMillis()
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
            log.info("提取${filename}耗时: ${System.currentTimeMillis() - time} ms")
            return resExt
        }

        private fun supportExtractFormat(s: String): Boolean {
            return Regex(""".*zip$|.*tar$|.*7z$""").matches(s)
        }
    }
}
