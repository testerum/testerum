package com.testerum.test_file_format.common.step_call.part

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonPatterns.NEWLINE
import com.testerum.common.parsing.util.CommonScanners.escapeSequence
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners
import org.jparsec.pattern.Patterns

object FileStepCallPartParserFactory : ParserFactory<FileStepCallPart> {

    override fun createParser() = stepCallPart()

    fun stepCallPart(): Parser<FileStepCallPart> {
        return or(
                argStepDefStepCallPart(),
                textStepDefStepCallPart()
        )
    }

    private fun textStepDefStepCallPart(): Parser<FileTextStepCallPart> {
        return Patterns.and(NEWLINE.not(), Patterns.notString("<<"))
                .many1()
                .toScanner("textStepDefStepCallPart")
                .source()
                .map { text -> FileTextStepCallPart(text) }
    }

    private fun argStepDefStepCallPart(): Parser<FileArgStepCallPart> {
        return sequence(
                Scanners.string("<<"),
                or(escapeSequence(), notEndOfArgStepCallPart()).many(),
                Scanners.string(">>")
        ) { _, texts, _ -> FileArgStepCallPart(texts.joinToString(separator = "")) }
    }

    private fun notEndOfArgStepCallPart(): Parser<String> {
        return Patterns.and(NEWLINE.not(), Patterns.notString(">>"))
                .toScanner("(not newline or >>)")
                .source()
    }

}