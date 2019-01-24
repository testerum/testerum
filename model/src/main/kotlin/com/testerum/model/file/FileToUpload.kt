package com.testerum.model.file

import java.io.InputStream

data class FileToUpload(val originalFileName: String?,
                        val inputStream: InputStream)
