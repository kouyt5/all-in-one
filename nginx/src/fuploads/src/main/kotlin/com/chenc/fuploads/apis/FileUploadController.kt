package com.chenc.fuploads.apis

import com.chenc.fuploads.service.StorageService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api")
class FileUploadController(private val storage: StorageService) {
    @GetMapping("/health")
    fun health() = mapOf("status" to "ok")

    @PostMapping("/uploads")
    fun uploads(@RequestParam("file") files: Array<MultipartFile>,
                @RequestParam("path", defaultValue = "/") path: String): ResponseEntity<Any> {
        require(files.isNotEmpty() && files.size <= 20) { "每次上传 1 至 20 个文件" }
        val success = mutableListOf<String>()
        val failed = mutableListOf<String>()
        for (file in files) {
            try { success.add(storage.upload(file, path)) }
            catch (e: Exception) { failed.add(file.originalFilename ?: "unknown") }
        }
        val code = if (failed.isEmpty()) 200 else 400
        return ResponseEntity.status(code).body(mapOf("code" to code,
            "message" to if (failed.isEmpty()) "OK" else "上传失败：请检查文件名、同名文件、路径、压缩包内容或磁盘空间",
            "data" to mapOf("successFileList" to success, "failedFileList" to failed)))
    }
}
