package com.testerum.test_file_format.feature

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import com.testerum.test_file_format.common.description.FileDescriptionParserFactory.description
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.stepCall
import com.testerum.test_file_format.common.tags.FileTagsParserFactory.tags
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import java.util.*

object FileFeatureParserFactory : ParserFactory<FileFeature> {

    override fun createParser(): Parser<FileFeature> = feature()

    fun feature(): Parser<FileFeature> {
        return sequence(
                featureDescription(),
                featureTags().asOptional(),
                featureHooks().asOptional()
        ) { description: Optional<String>, tags: Optional<List<String>>, hooks: Optional<List<HookWithType>> ->
            FileFeature(
                    description = description.orElse(null),
                    tags = tags.orElseGet { emptyList() },
                    beforeHooks = hooks.orElseGet { emptyList() }.filter { it.isBeforeHook  }.map { it.hook },
                    afterHooks  = hooks.orElseGet { emptyList() }.filter { !it.isBeforeHook }.map { it.hook }
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

    private fun featureHooks(): Parser<List<HookWithType>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                or(
                    stepCall("before-hook").map { beforeHook -> HookWithType(beforeHook, true) },
                    stepCall("after-hook").map { beforeHook -> HookWithType(beforeHook, false) }
                ),
                optionalWhitespaceOrNewLines()
        ) { _, hooks, _ -> hooks }.many()
    }

    private class HookWithType(val hook: FileStepCall, val isBeforeHook: Boolean)

}