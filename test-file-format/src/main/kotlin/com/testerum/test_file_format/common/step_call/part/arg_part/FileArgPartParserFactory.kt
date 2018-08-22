package com.testerum.test_file_format.common.step_call.part.arg_part

import com.testerum.common.parsing.ParserFactory
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPartParserFactory.expressionArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileTextArgPartParserFactory.textArgPart
import org.jparsec.Parser
import org.jparsec.Parsers.or

object FileArgPartParserFactory : ParserFactory<List<FileArgPart>> {

    // todo: tests for this class

    override fun createParser() = argParts()

    fun argParts(): Parser<List<FileArgPart>> {
        return argPart().many()
    }

    fun argPart(): Parser<FileArgPart> {
        return or(
                expressionArgPart(),
                textArgPart()
        )
    }

}
