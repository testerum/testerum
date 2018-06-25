package com.testerum.test_file_format.stepdef

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners.optionalNewLines
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.test_file_format.common.description.FileDescriptionParserFactory.description
import com.testerum.test_file_format.common.step_call.FileStepCallParserFactory.stepCall
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignatureParserFactory.stepDefSignature
import org.jparsec.Parser
import org.jparsec.Parsers.sequence

object FileStepDefParserFactory : ParserFactory<FileStepDef> {

    override fun createParser() = stepDef()

    fun stepDef(): Parser<FileStepDef> {
        return sequence(
                stepDefSignature(),
                optionalNewLines(),
                optionalWhitespace(),
                description().asOptional(),
                sequence(
                        optionalNewLines(),
                        optionalWhitespace(),
                        stepCall(),
                        optionalNewLines()
                ) { _, _, step, _ -> step }.many()
        ) { signature, _, _, description, steps -> FileStepDef(signature, description.orElse(null), steps) }
    }

}