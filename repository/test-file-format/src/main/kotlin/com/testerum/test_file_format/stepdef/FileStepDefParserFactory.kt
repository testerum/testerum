package com.testerum.test_file_format.stepdef

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners
import com.testerum.common.parsing.util.CommonScanners.optionalNewLines
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import com.testerum.test_file_format.common.description.FileDescriptionParserFactory.description
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.stepCall
import com.testerum.test_file_format.common.tags.FileTagsParserFactory
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignatureParserFactory.stepDefSignature
import com.testerum.test_file_format.testdef.FileTestDefParserFactory
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import java.util.*

object FileStepDefParserFactory : ParserFactory<FileStepDef> {

    override fun createParser() = stepDef()

    fun stepDef(): Parser<FileStepDef> {
        return sequence(
                stepDefSignature(),
                stepDefDescription(),
                stepDefTags(),
                stepDefStepCalls()
        ) { signature, description, tags, steps -> FileStepDef(signature, description, tags, steps) }
    }

    private fun stepDefDescription(): Parser<String?> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                description().asOptional(),
                optionalWhitespaceOrNewLines()
        ) {_, description, _ -> description.orElse(null) }
    }

    private fun stepDefStepCalls(): Parser<List<FileStepCall>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                optionalWhitespace(),
                stepCall(),
                optionalWhitespaceOrNewLines()
        ) { _, _, step, _ -> step }.many()
    }

    private fun stepDefTags(): Parser<List<String>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                FileTagsParserFactory.tags().asOptional(),
                optionalWhitespaceOrNewLines()
        ) { _: Void?, tags: Optional<List<String>>, _: Void? -> tags.orElse(emptyList()) }
    }

}