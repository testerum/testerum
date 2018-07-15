package com.testerum.test_file_format.testdef

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonPatterns.NOT_NEWLINE
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import com.testerum.test_file_format.common.description.FileDescriptionParserFactory.description
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.stepCall
import com.testerum.test_file_format.common.tags.FileTagsParserFactory.tags
import org.jparsec.Parser
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
        ) { _, testName, description, tags, steps -> FileTestDef(testName, description, tags, steps) }
    }

    private fun testDefKeyword(): Parser<Void> {
        return sequence(
                string("test-def:"),
                string(" ")
        )
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

}
