package com.testerum.test_file_format.manual_test_plan

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonParsers
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import com.testerum.test_file_format.common.description.FileDescriptionParserFactory
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import java.time.LocalDateTime
import java.util.*

object FileManualTestPlanParserFactory : ParserFactory<FileManualTestPlan> {

    override fun createParser(): Parser<FileManualTestPlan> = manualTestPlan()

    fun manualTestPlan(): Parser<FileManualTestPlan> {
        return sequence(
                description(),
                localDateTime("created-date-utc"),
                boolean("is-finalized"),
                localDateTime("finalized-date-utc")
        ) { description: Optional<String>, createdDateUtc: Optional<LocalDateTime>, isFinalized: Optional<Boolean>, finalizedDateUtc: Optional<LocalDateTime> ->
            FileManualTestPlan(
                    description = description.orElse(null),
                    createdDateUtc = createdDateUtc.orElse(null),
                    isFinalized = isFinalized.orElse(FileManualTestPlan.IS_FINALIZED_DEFAULT),
                    finalizedDateUtc = finalizedDateUtc.orElse(null)
            )
        }
    }

    private fun description(): Parser<Optional<String>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                FileDescriptionParserFactory.description().asOptional(),
                optionalWhitespaceOrNewLines()
        ) {_: Void?, description: Optional<String>, _: Void? -> description }
    }

    private fun localDateTime(label: String): Parser<Optional<LocalDateTime>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                CommonParsers.localDateTime(label).asOptional(),
                optionalWhitespaceOrNewLines()
        ) {_: Void?, createdDateUtc: Optional<LocalDateTime>, _: Void? -> createdDateUtc }
    }

    private fun boolean(label: String): Parser<Optional<Boolean>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                CommonParsers.boolean(label).asOptional(),
                optionalWhitespaceOrNewLines()
        ) {_: Void?, flag: Optional<Boolean>, _: Void? -> flag }
    }

}