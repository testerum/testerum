package com.testerum.test_file_format.testdef.scenarios

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonPatterns
import com.testerum.common.parsing.util.CommonScanners
import com.testerum.test_file_format.common.util.TestFileFormatScanners
import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners

object FileScenarioParserFactory : ParserFactory<FileScenario> {

    override fun createParser(): Parser<FileScenario> = testScenario()

    fun testScenarios(): Parser<List<FileScenario>> {
        return Parsers.sequence(
                CommonScanners.optionalWhitespaceOrNewLines(),
                testScenario(),
                CommonScanners.optionalWhitespaceOrNewLines()
        ) { _, scenario, _ -> scenario }.many()
    }

    fun testScenario(): Parser<FileScenario> {
        return Parsers.sequence(
                Scanners.string("scenario:"),
                Parsers.sequence(
                        Scanners.string(" "),
                        testScenarioName()
                ).asOptional(),
                testScenarioParams()
        ) { _, optionalName, params -> FileScenario(name = optionalName.orElse(null), params = params) }
    }

    private fun testScenarioName(): Parser<String> {
        return CommonPatterns.NOT_NEWLINE
                .many1()
                .toScanner("testScenarioName")
                .source()
    }

    private fun testScenarioParams(): Parser<List<FileScenarioParam>> {
        return Parsers.sequence(
                CommonScanners.atLeastOneNewLine(),
                CommonScanners.optionalWhitespace(),
                testScenarioParam()
        ).atomic()
                .many()
                .optional(emptyList())
    }

    private fun testScenarioParam(): Parser<FileScenarioParam> {
        return Parsers.sequence(
                Parsers.or(
                        Scanners.string("param-json").source(),
                        Scanners.string("param").source()
                ).source(),
                CommonScanners.optionalWhitespace(),
                TestFileFormatScanners.variableName(),
                CommonScanners.optionalWhitespace(),
                Scanners.string("="),
                CommonScanners.optionalWhitespace(),
                TestFileFormatScanners.multiLineAngleText()
        ) { paramType, _, paramName, _, _, _, paramValue ->
            val scenarioParamType = if (paramType == "param") {
                FileScenarioParamType.TEXT
            } else if (paramType == "param-json") {
                FileScenarioParamType.JSON
            } else {
                throw IllegalStateException("unknown param type [$paramType]")
            }

            FileScenarioParam(
                    name = paramName,
                    type = scenarioParamType,
                    value = paramValue
            )
        }
    }


}
