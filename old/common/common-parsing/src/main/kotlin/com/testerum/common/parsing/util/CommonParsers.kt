package com.testerum.common.parsing.util

import com.testerum.common.parsing.util.CommonScanners.notNewLine
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CommonParsers {

    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun localDateTime(label: String): Parser<LocalDateTime> {
        return sequence(
                string(label),
                optionalWhitespace(),
                string("="),
                notNewLine(),
                optionalWhitespaceOrNewLines()
        ) { _, _, _, value, _ ->
            LocalDateTime.parse(
                    value.trim(),
                    DATE_FORMATTER
            )
        }
    }

    fun serializeLocalDateTime(label: String, localDateTime: LocalDateTime): String {
        return "$label = ${DATE_FORMATTER.format(localDateTime)}"
    }

    fun boolean(label: String): Parser<Boolean> {
        return sequence(
                string(label),
                optionalWhitespace(),
                string("="),
                notNewLine(),
                optionalWhitespaceOrNewLines()
        ) { _, _, _, value, _ ->
            value.trim().toBoolean()
        }
    }

    fun serializeBoolean(label: String, flag: Boolean): String {
        return "$label = $flag"
    }

}
