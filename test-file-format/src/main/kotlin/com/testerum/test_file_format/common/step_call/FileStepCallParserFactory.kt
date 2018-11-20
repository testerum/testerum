package com.testerum.test_file_format.common.step_call

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners
import com.testerum.common.parsing.util.CommonScanners.optionalNewLines
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.`var`.FileStepVarParserFactory
import com.testerum.test_file_format.common.step_call.part.FileStepCallPartParserFactory.stepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhaseParserFactory.stepPhase
import com.testerum.test_file_format.manual_step_call.FileManualStepCall
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallStatus
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallStatusParserFactory.manualStepStatus
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string

object FileStepCallParserFactory : ParserFactory<FileStepCall> {

    override fun createParser() = stepCall()

    fun stepCall(): Parser<FileStepCall> {
        return stepCall("step")
    }

    fun stepCall(prefix: String): Parser<FileStepCall> {
        return sequence(
                string("$prefix:"),
                string(" "),
                stepPhase(),
                string(" "),
                stepCallPart().many1(),
                stepVars(),
                optionalNewLines()
        ) { _, _, phase, _, parts, vars, _ -> FileStepCall(phase, parts, vars) }
    }

    fun manualStepCall(): Parser<FileManualStepCall> {
        return sequence(
                sequence(
                        string("step "),
                        optionalWhitespace()
                ),
                manualStepStatus(),
                sequence(
                        optionalWhitespace(),
                        string(": ")
                ),
                stepPhase(),
                string(" "),
                stepCallPart().many1(),
                stepVars(),
                optionalNewLines()
        ) { _, stepCallStatus: FileManualStepCallStatus, _, phase, _, parts, vars, _ ->
            FileManualStepCall(
                    step = FileStepCall(phase, parts, vars),
                    status = stepCallStatus
            )
        }
    }

    private fun stepVars(): Parser<List<FileStepVar>> {
        return sequence(
                CommonScanners.atLeastOneNewLine(),
                CommonScanners.optionalWhitespace(),
                FileStepVarParserFactory.stepVar()
        ).atomic()
                .many()
                .optional(emptyList())
    }


}