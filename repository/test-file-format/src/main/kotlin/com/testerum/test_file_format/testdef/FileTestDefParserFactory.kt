package com.testerum.test_file_format.testdef

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonPatterns.NOT_NEWLINE
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import com.testerum.test_file_format.common.description.FileDescriptionParserFactory.description
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.stepCall
import com.testerum.test_file_format.common.tags.FileTagsParserFactory.tags
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string
import java.util.*

object FileTestDefParserFactory : ParserFactory<FileTestDef> {

    override fun createParser(): Parser<FileTestDef> = testDef()

    fun testDef(): Parser<FileTestDef> {
        return sequence(
                testDefKeyword(),
                testName(),
                testDescription(),
                testTags(),
                testStepCalls()
        ) { testType, testName, description, tags, steps -> FileTestDef(testName, testType == TestType.MANUAL, description, tags, steps) }
    }

    private fun testDefKeyword(): Parser<TestType> {
        return sequence(
                or(
                        string("test-def:").source(),
                        string("manual-test-def:").source()
                ),
                string(" ")
        ) { header, _ -> if (header == "test-def:") TestType.AUTOMATIC else TestType.MANUAL }
    }

    private fun testName() : Parser<String> {
        return NOT_NEWLINE
                .many1()
                .toScanner("testName")
                .source()
    }

    private fun testDescription(): Parser<String?> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                description().asOptional(),
                optionalWhitespaceOrNewLines()
        ) {_, description, _ -> description.orElse(null) }
    }

    private fun testTags(): Parser<List<String>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                tags().asOptional(),
                optionalWhitespaceOrNewLines()
        ) {_: Void?, tags: Optional<List<String>>, _: Void? -> tags.orElse(emptyList()) }
    }

    private fun testStepCalls(): Parser<List<FileStepCall>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                stepCall(),
                optionalWhitespaceOrNewLines()
        ) { _, step, _ -> step }.many()
    }

    private enum class TestType {
        AUTOMATIC,
        MANUAL,
        ;
    }

}
