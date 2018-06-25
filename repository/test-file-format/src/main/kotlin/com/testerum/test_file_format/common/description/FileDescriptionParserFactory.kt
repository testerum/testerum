package com.testerum.test_file_format.common.description

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.test_file_format.common.util.TestFileFormatScanners.multiLineAngleText
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string

object FileDescriptionParserFactory : ParserFactory<String> {

    override fun createParser() = description()

    fun description(): Parser<String> {
        return sequence(
                string("description"),
                optionalWhitespace(),
                string("="),
                optionalWhitespace(),
                multiLineAngleText()
        ) { _, _, _, _, descriptionText -> descriptionText }
    }

}