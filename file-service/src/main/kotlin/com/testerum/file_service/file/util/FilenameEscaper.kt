package com.testerum.file_service.file.util

import com.testerum.model.infrastructure.path.Path

object FilenameEscaper {

    private val REGEX_TO_REPLACE_ILLEGAL_FILE_REGEX = Regex("[^a-zA-Z0-9-\\s\\[\\]]")
    private const val REPLACEMENT_CHAR_OF_ILLEGAL_FILE_CHARS = "_"

    fun escape(pathPart: String): String = pathPart.replace(REGEX_TO_REPLACE_ILLEGAL_FILE_REGEX, REPLACEMENT_CHAR_OF_ILLEGAL_FILE_CHARS)
}

fun String.escapeFileOrDirectoryName() = FilenameEscaper.escape(this)

fun Path.escape(): Path {
    return Path(
            directories = this.directories.map { it.escapeFileOrDirectoryName() },
            fileName = this.fileName?.escapeFileOrDirectoryName(),
            fileExtension = this.fileExtension
    )
}


