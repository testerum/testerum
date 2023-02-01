package com.testerum.test_file_format.manual_step_call.status

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string

object FileManualStepCallPropertiesParserFactory : ParserFactory<FileManualStepCallProperties> {

    private val statuses: List<String> = run {
        val result = mutableListOf<String>()

        for (status in FileManualStepCallStatus.values()) {
            result += status.name
        }

        result
    }

    override fun createParser(): Parser<FileManualStepCallProperties> = manualStepProperties()

    fun manualStepProperties(): Parser<FileManualStepCallProperties> {
        return sequence(
                string("["),
                or(
                        statuses.map {
                            string(it).source()
                        }
                ),
                sequence(
                        string(","),
                        CommonScanners.optionalWhitespace(),
                        string("disabled").source()
                ).asOptional(),
                string("]")
        ) { _, statusText, disabled, _ ->
            FileManualStepCallProperties(
                    status = FileManualStepCallStatus.valueOf(statusText),
                    enabled = !disabled.isPresent
            )
         }
    }

}
