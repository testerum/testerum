package com.testerum.test_file_format.testdef

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonPatterns.NOT_NEWLINE
import com.testerum.common.parsing.util.CommonScanners.optionalNewLines
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.test_file_format.common.description.FileDescriptionParserFactory.description
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.stepCall
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string

object FileTestDefParserFactory : ParserFactory<FileTestDef> {

    override fun createParser(): Parser<FileTestDef> = testDef()

    fun testDef(): Parser<FileTestDef> {
        return sequence(
                string("test-def:"),
                string(" "),
                testName(),
                optionalNewLines(),
                optionalWhitespace(),
                description().asOptional(),
                sequence(
                        optionalNewLines(),
                        optionalWhitespace(),
                        stepCall(),
                        optionalNewLines()
                ) { _, _, step, _ -> step }.many()
        ) { _, _, testName, _, _, description, steps -> FileTestDef(testName, description.orElse(null), steps) }
    }

    private fun testName() : Parser<String> {
        return NOT_NEWLINE
                .many1()
                .toScanner("testName")
                .source()
                .map { it }
    }
}
