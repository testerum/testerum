package com.testerum.test_file_format.common.step_call.phase

import com.testerum.common.parsing.ParserFactory
import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners

object FileStepPhaseParserFactory : ParserFactory<FileStepPhase> {

    override fun createParser() = stepPhase()

    fun stepPhase(): Parser<FileStepPhase> {
        return Parsers.or(
                FileStepPhase.codes.map {
                    Scanners.string(it).source()
                }
        ).map {
            FileStepPhase.getByCode(it)
        }
    }

}