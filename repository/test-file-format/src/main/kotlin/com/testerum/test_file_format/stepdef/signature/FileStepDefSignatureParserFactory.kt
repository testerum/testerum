package com.testerum.test_file_format.stepdef.signature

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonPatterns.NEWLINE
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.test_file_format.common.step_call.phase.FileStepPhaseParserFactory.stepPhase
import com.testerum.test_file_format.common.util.TestFileFormatScanners.variableName
import com.testerum.test_file_format.common.util.TestFileFormatScanners.variableType
import com.testerum.test_file_format.stepdef.signature.part.FileParamStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileTextStepDefSignaturePart
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string
import org.jparsec.pattern.Patterns

object FileStepDefSignatureParserFactory : ParserFactory<FileStepDefSignature> {

    override fun createParser() = stepDefSignature()

    fun stepDefSignature(): Parser<FileStepDefSignature> {
        return sequence(
                string("step-def:"),
                string(" "),
                stepPhase(),
                string(" "),
                stepDefSignaturePart().many1()
        ) { _, _, phase, _, parts -> FileStepDefSignature(phase, parts) }
    }

    private fun stepDefSignaturePart() : Parser<FileStepDefSignaturePart> {
        return or(
                argStepDefSignaturePart(),
                textStepDefSignaturePart()
        )
    }

    private fun textStepDefSignaturePart() : Parser<FileTextStepDefSignaturePart> {
        return Patterns.and(NEWLINE.not(), Patterns.notString("<<"))
                .many1()
                .toScanner("textStepDefSignaturePart")
                .source()
                .map { text -> FileTextStepDefSignaturePart(text) }
    }

    private fun argStepDefSignaturePart() : Parser<FileParamStepDefSignaturePart> {
        return sequence(
                string("<<"),
                sequence(optionalWhitespace(), variableName(), optionalWhitespace()) { _, varName, _ -> varName },
                string(":"),
                sequence(optionalWhitespace(), variableType(), optionalWhitespace()) { _, varType, _ -> varType },
                string(">>")
        ) { _, varName, _, varType, _ -> FileParamStepDefSignaturePart(varName, varType) }
    }

}
