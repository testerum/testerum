package com.testerum.runner_cmdline.cmdline.params

object ArgsFileParser {

    fun parseArgsFile(fileContent: String): List<String> {
        val result = mutableListOf<String>()

        val length = fileContent.length
        val lastIndex = length - 1

        var argBuilder = newStringBuilder()
        var i = 0
        var startIndex = 0
        while (i < length) {
            val c = fileContent[i]
            if (c == '\\' && (i < lastIndex) && fileContent[i + 1] == '\\') {
                // escaped backslash
                argBuilder.append(fileContent, startIndex, i)
                argBuilder.append('\\')
                startIndex = i + 2
                i += 2
            } else if (c == '\\' && (i < lastIndex) && fileContent[i + 1] == '\n') {
                // escaped newline
                argBuilder.append(fileContent, startIndex, i)
                startIndex = i + 1
                i += 2
            } else if (c == '\n') {
                // normal (unescaped) newline
                argBuilder.append(
                    fileContent.substring(startIndex, i)
                )
                result += argBuilder.toString()
                argBuilder = newStringBuilder()
                startIndex = i + 1
                i++
            } else {
                i++
            }
        }

        if (startIndex < length) {
            argBuilder.append(
                fileContent.substring(startIndex)
            )
        }
        if (argBuilder.isNotEmpty()) {
            result += argBuilder.toString()
        }

        return result
    }

    private fun newStringBuilder() = StringBuilder(2 * 1024)

}
