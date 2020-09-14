package com.testerum.report_generators.reports.report_model.json_model

import com.testerum.report_generators.reports.report_model.base.BaseReportModelExecutionListener
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

class JsonModelExecutionListener(private val properties: Map<String, String>) : BaseReportModelExecutionListener() {

    private val _destinationDirectory: JavaPath = run {
        val destinationDirectoryProperty = properties[EventListenerProperties.JsonModel.DESTINATION_DIRECTORY]
                ?: throw IllegalArgumentException("missing required property ${EventListenerProperties.JsonModel.DESTINATION_DIRECTORY}")

        return@run Paths.get(destinationDirectoryProperty).toAbsolutePath().normalize()
    }

    override val destinationDirectory: JavaPath
        get() = _destinationDirectory

    private val _formatted: Boolean = run {
        val formattedProperty = properties[EventListenerProperties.JsonModel.FORMATTED]
                ?: return@run false

        return@run formattedProperty.toBoolean()
    }

    override val formatted: Boolean
        get() = _formatted

}
