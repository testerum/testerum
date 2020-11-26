package com.testerum.scanner.step_lib_scanner.step_pattern_parser

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.ParamSimpleBasicStepPatternPart
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.SimpleBasicStepPatternPart
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.TextSimpleBasicStepPatternPart
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ScannerStepPatternParserFactoryTest {

    private val parser = ParserExecuter(
        ScannerStepPatternParserFactory.pattern()
    )

    @Test
    fun `should parse simple pattern`() {
        assertThat(parser.parse("simple pattern"))
            .isEqualTo(
                listOf<SimpleBasicStepPatternPart>(
                    TextSimpleBasicStepPatternPart(text = "simple pattern")
                )
            )
    }

    @Test
    fun `should parse pattern with parts of different types`() {
        assertThat(parser.parse("I login as '<<username>>'/'<<password>>' to '<<loginUrl>>'"))
            .isEqualTo(
                listOf(
                    TextSimpleBasicStepPatternPart(text = "I login as '"),
                    ParamSimpleBasicStepPatternPart(name = "username"),
                    TextSimpleBasicStepPatternPart(text = "'/'"),
                    ParamSimpleBasicStepPatternPart(name = "password"),
                    TextSimpleBasicStepPatternPart(text = "' to '"),
                    ParamSimpleBasicStepPatternPart(name = "loginUrl"),
                    TextSimpleBasicStepPatternPart(text = "'")
                )
            )
    }

}
