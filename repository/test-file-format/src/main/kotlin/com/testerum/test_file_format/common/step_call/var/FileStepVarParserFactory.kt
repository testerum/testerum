package com.testerum.test_file_format.common.step_call.`var`

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.test_file_format.common.util.TestFileFormatScanners.multiLineAngleText
import com.testerum.test_file_format.common.util.TestFileFormatScanners.variableName
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string

object FileStepVarParserFactory : ParserFactory<FileStepVar> {

    override fun createParser() = stepVar()

    fun stepVar(): Parser<FileStepVar> {
        return sequence(
                string("var"),
                optionalWhitespace(),
                variableName(),
                optionalWhitespace(),
                string("="),
                optionalWhitespace(),
                multiLineAngleText()
        ) { _, _, varName, _, _, _, varValue -> FileStepVar(varName, varValue) }
    }

}