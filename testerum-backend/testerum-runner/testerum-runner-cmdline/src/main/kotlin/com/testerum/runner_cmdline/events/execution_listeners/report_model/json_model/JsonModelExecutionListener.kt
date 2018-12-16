package com.testerum.runner_cmdline.events.execution_listeners.report_model.json_model

import com.fasterxml.jackson.databind.ObjectWriter
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.writeText
import com.testerum.runner.cmdline.EventListenerProperties
import com.testerum.runner.report_model.ReportSuite
import com.testerum.runner_cmdline.events.execution_listeners.report_model.BaseReportModelExecutionListener
import com.testerum.runner_cmdline.transformer.builtin.lang.BooleanTransformer
import java.nio.file.Path
import java.nio.file.Paths

class JsonModelExecutionListener(private val properties: Map<String, String>) : BaseReportModelExecutionListener() {

    private val destinationFile: Path? = run {
        val destinationFileNameProperty = properties[EventListenerProperties.JsonModel.DESTINATION_FILE_NAME]
                ?: return@run null

        return@run Paths.get(destinationFileNameProperty).toAbsolutePath().normalize()
    }

    private val formatted: Boolean = run {
        val formattedProperty = properties[EventListenerProperties.JsonModel.FORMATTED]
                ?: return@run false

        return@run BooleanTransformer.transform(formattedProperty)
    }

    override fun handleReportModel(reportSuite: ReportSuite) {
        // serialize model
        val objectWriter: ObjectWriter = if (formatted) {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
        } else {
            OBJECT_MAPPER.writer()
        }
        val serializedModel = objectWriter.writeValueAsString(reportSuite)

        // write data file
        if (destinationFile == null) {
            println(serializedModel)
        } else {
            destinationFile.parent?.createDirectories()
            destinationFile.writeText(serializedModel)
        }
    }

}
