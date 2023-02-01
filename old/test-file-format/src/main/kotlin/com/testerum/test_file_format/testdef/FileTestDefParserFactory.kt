package com.testerum.test_file_format.testdef

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonPatterns.NOT_NEWLINE
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import com.testerum.test_file_format.common.description.FileDescriptionParserFactory.description
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.afterHookCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.manualStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.stepCall
import com.testerum.test_file_format.common.tags.FileTagsParserFactory.tags
import com.testerum.test_file_format.manual_step_call.FileManualStepCall
import com.testerum.test_file_format.manual_test.FileManualTestDef
import com.testerum.test_file_format.manual_test.comments.FileManualCommentsParserFactory
import com.testerum.test_file_format.manual_test.status.FileManualTestStatus
import com.testerum.test_file_format.manual_test.status.FileManualTestStatusParserFactory
import com.testerum.test_file_format.testdef.properties.FileTestDefProperties
import com.testerum.test_file_format.testdef.properties.FileTestDefPropertiesParserFactory.testProperties
import com.testerum.test_file_format.testdef.scenarios.FileScenarioParserFactory.testScenarios
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
                testScenarios(),
                testStepCalls(),
                testAfterHooks(),
        ) { _, testName, properties, description, tags, scenarios, steps, afterHooks -> FileTestDef(testName, properties, description, tags, scenarios, steps, afterHooks) }
    }

    fun manualTestDef(): Parser<FileManualTestDef> {
        return sequence(
                testDefKeyword(),
                testName(),
                testDescription(),
                testTags(),
                manualTestStepCalls(),
                manualTestStatus(),
                manualTestComments()
        ) { _, testName, description, tags, steps, testStatus, comments ->
            FileManualTestDef(
                    name = testName,
                    description = description,
                    tags = tags,
                    stepCalls = steps,
                    status = testStatus,
                    comments = comments
            )
        }
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

    private fun testAfterHooks(): Parser<List<FileStepCall>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                afterHookCall(),
                optionalWhitespaceOrNewLines()
        ) { _, step, _ -> step }.many()
    }

    private fun manualTestStepCalls(): Parser<List<FileManualStepCall>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                manualStepCall(),
                optionalWhitespaceOrNewLines()
        ) { _, step, _ -> step }.many()
    }

    private fun manualTestStatus(): Parser<FileManualTestStatus> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                FileManualTestStatusParserFactory.manualTestStatus(),
                optionalWhitespaceOrNewLines()
        ) {_, testStatus, _ -> testStatus }
    }

    private fun manualTestComments(): Parser<String?> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                FileManualCommentsParserFactory.manualTestComments().asOptional(),
                optionalWhitespaceOrNewLines()
        ) {_, comments, _ -> comments.orElse(null) }
    }

}
