package com.testerum.test_file_format.manual_step_call.status

import com.testerum.common.parsing.ParserFactory
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string

object FileManualStepCallStatusParserFactory : ParserFactory<FileManualStepCallStatus> {

    override fun createParser(): Parser<FileManualStepCallStatus> = manualStepStatus()

    fun manualStepStatus(): Parser<FileManualStepCallStatus> {
        return sequence(
                string("["),
                or(
                        FileManualStepCallStatus.values().map {
                            string(it.name).source()
                        }
                ),
                string("]")
        ) { _, statusText, _ -> FileManualStepCallStatus.valueOf(statusText) }
    }

}