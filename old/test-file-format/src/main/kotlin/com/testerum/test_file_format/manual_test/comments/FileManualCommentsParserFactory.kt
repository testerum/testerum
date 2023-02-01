package com.testerum.test_file_format.manual_test.comments

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import com.testerum.test_file_format.common.util.TestFileFormatScanners.multiLineAngleText
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string

object FileManualCommentsParserFactory : ParserFactory<String> {

    override fun createParser() = manualTestComments()

    fun manualTestComments(): Parser<String> {
        return sequence(
                string("comments"),
                optionalWhitespace(),
                string("="),
                optionalWhitespace(),
                multiLineAngleText(),
                optionalWhitespaceOrNewLines()
        ) { _, _, _, _, descriptionText, _ -> descriptionText }
    }

}