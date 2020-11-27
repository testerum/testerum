package com.testerum.common.parsing.util

import org.jparsec.pattern.Pattern
import org.jparsec.pattern.Patterns

object CommonPatterns {

    val NEWLINE: Pattern = Patterns.regex("\r\n|\n")
    val NOT_NEWLINE: Pattern = Patterns.isChar { it != '\n' && it != '\r' }

    val LOWERCASE_LETTER: Pattern = Patterns.range('a', 'z')
    val UPPERCASE_LETTER: Pattern = Patterns.range('A', 'Z')

    val INTEGER: Pattern = Patterns.or(
        Patterns.string("0"),
        Patterns.range('1', '9')
            .next(Patterns.range('0', '9').many())
    )
}
