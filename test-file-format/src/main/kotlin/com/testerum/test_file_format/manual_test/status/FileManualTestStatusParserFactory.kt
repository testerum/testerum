package com.testerum.test_file_format.manual_test.status

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string

object FileManualTestStatusParserFactory : ParserFactory<FileManualTestStatus> {

    override fun createParser(): Parser<FileManualTestStatus> = manualTestStatus()

    fun manualTestStatus(): Parser<FileManualTestStatus> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                string("status"),
                optionalWhitespace(),
                string("="),
                optionalWhitespace(),
                or(
                        FileManualTestStatus.values().map {
                            string(it.name).source()
                        }
                ),
                optionalWhitespaceOrNewLines()
        ) { _, _, _, _, _, statusAsText, _ -> FileManualTestStatus.valueOf(statusAsText) }
    }

}