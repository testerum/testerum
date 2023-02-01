package com.testerum.scanner.step_lib_scanner.step_pattern_parser

import com.testerum.common.parsing.ParserFactory
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.ParamSimpleBasicStepPatternPart
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.SimpleBasicStepPatternPart
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.TextSimpleBasicStepPatternPart
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners
import org.jparsec.pattern.Patterns

object ScannerStepPatternParserFactory : ParserFactory<List<SimpleBasicStepPatternPart>> {

    override fun createParser() = pattern()

    fun pattern(): Parser<List<SimpleBasicStepPatternPart>> {
        return patternPart().many1()
    }

    private fun patternPart(): Parser<SimpleBasicStepPatternPart> {
        return or(
                paramPatternPart(),
                textPatternPart()
        )
    }

    private fun paramPatternPart(): Parser<ParamSimpleBasicStepPatternPart> {
        return sequence(
                Scanners.string("<<"),
                Patterns.notString(">>").many1().toScanner("not >>").source(),
                Scanners.string(">>")
        ) { _, paramName, _ -> ParamSimpleBasicStepPatternPart(paramName) }
    }

    private fun textPatternPart(): Parser<TextSimpleBasicStepPatternPart> {
        return Patterns.notString("<<")
                .many1()
                .toScanner("textPatternPart")
                .source()
                .map { text -> TextSimpleBasicStepPatternPart(text) }
    }

}