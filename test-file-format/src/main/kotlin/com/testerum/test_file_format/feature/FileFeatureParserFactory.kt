package com.testerum.test_file_format.feature

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import com.testerum.test_file_format.common.description.FileDescriptionParserFactory.description
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.stepCall
import com.testerum.test_file_format.common.tags.FileTagsParserFactory.tags
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import java.util.*

object FileFeatureParserFactory : ParserFactory<FileFeature> {

    override fun createParser(): Parser<FileFeature> = feature()

    fun feature(): Parser<FileFeature> {
        return sequence(
                featureDescription(),
                featureTags().asOptional(),
                featureHook("before-all-hook").asOptional(),
                featureHook("before-each-hook").asOptional(),
                featureHook("after-each-hook").asOptional(),
                featureHook("after-all-hook").asOptional(),
        ) { description: Optional<String>, tags: Optional<List<String>>, beforeAll: Optional<List<FileStepCall>>, beforeEach: Optional<List<FileStepCall>>, afterEach: Optional<List<FileStepCall>>, afterAll: Optional<List<FileStepCall>> ->
            FileFeature(
                    description = description.orElse(null),
                    tags = tags.orElseGet { emptyList() },
                    beforeAllHooks = beforeAll.orElseGet { emptyList() },
                    beforeEachHooks = beforeEach.orElseGet { emptyList() },
                    afterEachHooks = afterEach.orElseGet { emptyList() },
                    afterAllHooks = afterAll.orElseGet { emptyList() }
            ) 
        }
    }

    private fun featureDescription(): Parser<Optional<String>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                description().asOptional(),
                optionalWhitespaceOrNewLines()
        ) {_: Void?, description: Optional<String>, _: Void? -> description }
    }

    private fun featureTags(): Parser<List<String>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                tags(),
                optionalWhitespaceOrNewLines()
        ) {_: Void?, tags: List<String>, _: Void? -> tags }
    }

    private fun featureHook(hookPrefix: String): Parser<List<FileStepCall>> {
        return sequence(
            optionalWhitespaceOrNewLines(),
            stepCall(hookPrefix),
            optionalWhitespaceOrNewLines()
        ) { _, step, _ -> step }.many()
    }
}
