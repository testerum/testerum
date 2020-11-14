package com.testerum.test_file_format.common.step_call

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners.atLeastOneNewLine
import com.testerum.common.parsing.util.CommonScanners.optionalNewLines
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.`var`.FileStepVarParserFactory
import com.testerum.test_file_format.common.step_call.part.FileStepCallPartParserFactory.stepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhaseParserFactory.stepPhase
import com.testerum.test_file_format.manual_step_call.FileManualStepCall
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallProperties
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallPropertiesParserFactory.manualStepProperties
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners
import org.jparsec.Scanners.string

object FileStepCallParserFactory : ParserFactory<FileStepCall> {

    override fun createParser() = stepCall()

    fun stepCall(): Parser<FileStepCall> {
        return stepCall("step")
    }

    //TODO Ionut-Cristi: why is this class a factory, why do we even need an interface? Should we do some refactoring?
    fun afterHookCall(): Parser<FileStepCall> {
        return stepCall("after-hook")
    }

    fun stepCall(prefix: String): Parser<FileStepCall> {
        return sequence(
                string(prefix),
                sequence(
                        optionalWhitespace(),
                        Scanners.string("[disabled]").source().asOptional()
                ),
                string(": "),
                stepPhase(),
                string(" "),
                stepCallPart().many1(),
                stepVars(),
                optionalNewLines()
        ) { _, disabled, _, phase, _, parts, vars, _ -> FileStepCall(phase, parts, vars, !disabled.isPresent) }
    }

    fun manualStepCall(): Parser<FileManualStepCall> {
        return sequence(
                sequence(
                        string("step "),
                        optionalWhitespace()
                ),
                manualStepProperties(),
                sequence(
                        optionalWhitespace(),
                        string(": ")
                ),
                stepPhase(),
                string(" "),
                stepCallPart().many1(),
                stepVars(),
                optionalNewLines()
        ) { _, stepCallProperties: FileManualStepCallProperties, _, phase, _, parts, vars, _ ->
            FileManualStepCall(
                    step = FileStepCall(phase, parts, vars, stepCallProperties.enabled),
                    status = stepCallProperties.status
            )
        }
    }

    private fun stepVars(): Parser<List<FileStepVar>> {
        return sequence(
                atLeastOneNewLine(),
                optionalWhitespace(),
                FileStepVarParserFactory.stepVar()
        ).atomic()
                .many()
                .optional(emptyList())
    }

}
