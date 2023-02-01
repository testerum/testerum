package com.testerum.web_backend.util

import com.testerum.model.file.FileToUpload
import org.springframework.web.multipart.MultipartFile

fun List<MultipartFile>.toFilesToUpload(): List<FileToUpload> = map { it.toFileToUpload() }

fun MultipartFile.toFileToUpload() = FileToUpload(originalFilename, inputStream)
