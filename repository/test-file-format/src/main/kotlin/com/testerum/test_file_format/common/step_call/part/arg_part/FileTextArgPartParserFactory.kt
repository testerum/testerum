package com.testerum.test_file_format.common.step_call.part.arg_part

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners.escapeSequence
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.pattern.Patterns

object FileTextArgPartParserFactory : ParserFactory<FileTextArgPart> {

    override fun createParser() = textArgPart()

    fun textArgPart(): Parser<FileTextArgPart> {
        return or(escapeSequence(), notEndOfTextArgPart())
                .many()
                .map { texts -> FileTextArgPart(texts.joinToString(separator = "")) }
    }

    private fun notEndOfTextArgPart(): Parser<String> {
        return Patterns.and(Patterns.notString(">>"), Patterns.notString("{{"))
                .toScanner("(not newline, >>, or {{)")
                .source()
    }

}