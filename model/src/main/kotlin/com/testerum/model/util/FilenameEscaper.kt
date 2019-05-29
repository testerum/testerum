package com.testerum.model.util

import com.testerum.model.infrastructure.path.Path
import org.apache.commons.lang3.StringUtils

object FilenameEscaper {

    private val REGEX_TO_REPLACE_ILLEGAL_FILE_REGEX = Regex("[^-_a-zA-Z0-9\\s\\[\\]]")
    private const val REPLACEMENT_CHAR_OF_ILLEGAL_FILE_CHARS = "_"
    private val RESERVED_FILE_NAMES: Set<String> = run {
        val result = HashSet<String>()

        result += "CON"
        result += "PRN"
        result += "AUX"
        result += "NUL"

        for (i in 1..9) {
            result += "COM$i"
        }
        for (i in 1..9) {
            result += "LPT$i"
        }

        return@run result
    }

    fun escape(pathPart: String): String {
        var result = pathPart

        result = replaceIllegalCharacters(result)
        result = replaceReservedNames(result)
        result = replaceIllegalEndings(result)
        result = shortenLongNames(result)

        return result
    }

    private fun replaceIllegalCharacters(pathPart: String): String = pathPart.replace(REGEX_TO_REPLACE_ILLEGAL_FILE_REGEX, REPLACEMENT_CHAR_OF_ILLEGAL_FILE_CHARS)

    private fun replaceReservedNames(pathPart: String): String {
        for (reservedName in RESERVED_FILE_NAMES) {
            // forbidden: both plain reserved names, and reserved names with extension, like "NUL.txt"; see https://docs.microsoft.com/en-us/windows/desktop/fileio/naming-a-file
            if (pathPart == reservedName || pathPart.startsWith("$reservedName.")) {
                return "${pathPart}_"
            }
        }

        return pathPart
    }

    private fun replaceIllegalEndings(pathPart: String): String {
        // filenames cannot end with dot "." or space " "; see https://docs.microsoft.com/en-us/windows/desktop/fileio/naming-a-file
        if (pathPart.isEmpty()) {
            return pathPart
        }

        val lastChar = pathPart.last()

        return if (lastChar == '.' || lastChar == ' ') {
            "${pathPart}_"
        } else {
            pathPart
        }
    }

    private fun shortenLongNames(pathPart: String): String {
        // NTFS (Windows), HSF+ (Mac), and EXT4 (Linux) have a max file name length of 255 characters (Windows/Mac) or 255 bytes (Linux/Mac with HSF)
        // Since we only allow ASCII characters, we will consider that 1 character = 1 byte, for the purpose of this method
        //
        // IMPORTANT: always call this method after replaceIllegalCharacters(), otherwise it won't work properly
        //

        return if (pathPart.length < 256) {
            pathPart
        } else {
            StringUtils.substring(pathPart, 0, 256)
        }
    }

}

fun String.escapeFileOrDirectoryName() = FilenameEscaper.escape(this)

fun Path.escape(): Path {
    return Path(
            directories = this.directories.map { it.escapeFileOrDirectoryName() },
            fileName = this.fileName?.escapeFileOrDirectoryName(),
            fileExtension = this.fileExtension
    )
}


