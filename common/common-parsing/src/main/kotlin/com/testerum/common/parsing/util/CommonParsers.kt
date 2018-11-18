package com.testerum.common.parsing.util

import com.testerum.common.parsing.util.CommonScanners.notNewLine
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string
import java.time.LocalDateTime

object CommonParsers {

    fun localDateTime(label: String): Parser<LocalDateTime> {
        return sequence(
                string(label),
                optionalWhitespace(),
                string("="),
                notNewLine(),
                optionalWhitespaceOrNewLines()
        ) { _, _, _, value, _ ->
            LocalDateTime.parse(
                    value.trim()
            )
        }
    }

}
