package com.chenc.fuploads

import com.chenc.fuploads.apis.FileUploadController
import com.chenc.fuploads.component.ExceptionCatcher
import com.chenc.fuploads.service.StorageService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UploadApiTests {
    @TempDir lateinit var directory: Path
    private fun mvc() = MockMvcBuilders.standaloneSetup(FileUploadController(StorageService(directory.toString())))
        .setControllerAdvice(ExceptionCatcher()).build()

    @Test fun `multipart response preserves contract and rejects overwrite`() {
        val mvc = mvc()
        val file = MockMultipartFile("file", "a.txt", "text/plain", "hello".toByteArray())
        mvc.perform(multipart("/api/uploads").file(file)).andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(200)).andExpect(jsonPath("$.data.successFileList[0]").value("a.txt"))
        mvc.perform(multipart("/api/uploads").file(file)).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.data.failedFileList[0]").value("a.txt"))
    }
    @Test fun `invalid multipart requests receive client errors`() {
        val mvc = mvc()
        mvc.perform(post("/api/uploads")).andExpect(status().isBadRequest)
        mvc.perform(multipart("/api/uploads")).andExpect(status().isBadRequest)
    }
}
