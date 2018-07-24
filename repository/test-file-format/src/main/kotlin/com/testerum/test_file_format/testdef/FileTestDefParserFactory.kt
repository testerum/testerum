package com.testerum.test_file_format.testdef

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonPatterns.NOT_NEWLINE
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import com.testerum.test_file_format.common.description.FileDescriptionParserFactory.description
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.stepCall
import com.testerum.test_file_format.common.tags.FileTagsParserFactory.tags
import com.testerum.test_file_format.testdef.properties.FileTestDefProperties
import com.testerum.test_file_format.testdef.properties.FileTestDefPropertiesParserFactory.testProperties
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
                testTestProperties(),
                testDescription(),
                testTags(),
                testStepCalls()
        ) { _, testName, properties, description, tags, steps -> FileTestDef(testName, properties, description, tags, steps) }
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

    private fun testTestProperties(): Parser<FileTestDefProperties> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                testProperties().asOptional(),
                optionalWhitespaceOrNewLines()
        ) {_: Void?, properties: Optional<FileTestDefProperties>, _: Void? -> properties.orElse(FileTestDefProperties.DEFAULT) }
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
