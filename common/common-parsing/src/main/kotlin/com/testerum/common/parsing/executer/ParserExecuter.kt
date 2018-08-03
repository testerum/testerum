package com.testerum.common.parsing.executer

import org.apache.commons.io.IOUtils
import org.jparsec.Parser
import org.jparsec.error.ParserException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

class ParserExecuter<T>(private val parser: Parser<T>) {

    companion object {
        private val NEWLINE_REGEX = Regex("\r\n|\n")
    }

    fun parse(inputStream: InputStream, charset: Charset = Charsets.UTF_8)
            = parse(
                    IOUtils.toString(inputStream, charset)
            )

    fun parse(text: String): T
            = try {
                parser.parse(text)
            } catch (e: ParserException) {
                throw ParserExecuterException(errorMessage(text, e), e)
            } catch (e: Exception) {
                throw ParserExecuterException("failed to parse [$text]", e)
            }

    private fun errorMessage(text: String, e: ParserException) = buildString {
        append("failed to parse")

        val location = e.location
        val details = e.errorDetails

        if (location != null && details != null) {
            append(":\n")
            append(text.split(NEWLINE_REGEX).subList(0, location.line).joinToString(separator = "\n")) // todo: check for IOOBE
            append("\n")

            val numberOfSpaces = if (location.column >= 1) {
                location.column - 1
            } else {
                0
            }
            append(" ".repeat(numberOfSpaces))
            append("^--- ERROR at line ${location.line}, column ${location.column}: ")

            val failureMessage = details.getFailureMessage()
            if (failureMessage != null) {
                append(failureMessage)
            } else if (details.expected != null) {
                append("[")
                reportList(this, details.expected)
                append("] expected, [")
                append(details.encountered).append("] encountered")
            } else if (details.unexpected != null) {
                append("unexpected ").append(details.unexpected)
            }
        }
    }

    private fun reportList(builder: StringBuilder, messages: List<String>) {
        if (messages.isEmpty()) return
        val set = LinkedHashSet(messages)
        val size = set.size
        var i = 0
        for (message in set) {
            if (i++ > 0) {
                if (i == size) { // last one
                    builder.append(" or ")
                } else {
                    builder.append(", ")
                }
            }
            builder.append(message)
        }
    }

}