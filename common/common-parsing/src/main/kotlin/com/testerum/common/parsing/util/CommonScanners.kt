package com.testerum.common.parsing.util

import com.testerum.common.parsing.util.CommonPatterns.NEWLINE
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Scanners
import org.jparsec.pattern.CharPredicates
import org.jparsec.pattern.Patterns

object CommonScanners {

    fun optionalWhitespace(): Parser<Void> = Patterns.many(CharPredicates.among(" \t")).toScanner("optionalWhitespace")
    fun optionalWhitespaceOrNewLines(): Parser<Void> = Patterns.many { c -> c == ' ' || c == '\t' || c == '\n' || c == '\r' }.toScanner("optionalWhitespaceOrNewLines")

    fun optionalNewLines(): Parser<MutableList<Void>> = newLine().many()
    fun atLeastOneNewLine(): Parser<MutableList<Void>> = newLine().many1()
    private fun newLine(): Parser<Void> = NEWLINE.toScanner("newline")

    fun escapeSequence(): Parser<String> {
        return or(
                escapeFor("<<"),
                escapeFor(">>"),
                escapeFor("{{"),
                escapeFor("}}")
        )
    }


    private fun escapeFor(text: String): Parser<String> {
        return Scanners.string("\\$text")
                .source()
                .map { it.substring(1) }
    }

}