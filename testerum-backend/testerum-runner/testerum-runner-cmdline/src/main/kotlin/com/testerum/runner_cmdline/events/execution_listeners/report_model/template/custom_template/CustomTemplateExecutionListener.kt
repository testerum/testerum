package com.testerum.runner_cmdline.events.execution_listeners.report_model.template.custom_template

import com.testerum.common_kotlin.deleteRecursivelyIfExists
import com.testerum.common_kotlin.doesNotExist
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.cmdline.report_type.marshaller.properties.RunnerPropertiesSerializer
import com.testerum.runner_cmdline.events.execution_listeners.report_model.base.BaseReportModelExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.utils.node.RunnerNodeExecuter
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

class CustomTemplateExecutionListener(private val properties: Map<String, String>) : BaseReportModelExecutionListener() {

    private val _destinationDirectory: JavaPath = run {
        return@run Files.createTempDirectory("testerum.runner-report-data")
    }

    override val destinationDirectory: JavaPath
        get() = _destinationDirectory

    override val formatted: Boolean
        get() = false


    private val scriptFile: JavaPath = run {
        val scriptFileProperty = properties[EventListenerProperties.CustomTemplate.SCRIPT_FILE]
                ?: throw IllegalArgumentException("missing required property ${EventListenerProperties.CustomTemplate.SCRIPT_FILE}")

        val scriptFilePath = Paths.get(scriptFileProperty)
        val normalizedScriptFilePath = scriptFilePath.toAbsolutePath().normalize()
        if (scriptFilePath.doesNotExist) {
            throw IllegalArgumentException("the file [$scriptFileProperty] (resolved as [$normalizedScriptFilePath]) does not exist")
        }

        return@run normalizedScriptFilePath
    }


    override fun afterModelSavedToFile() {
        try {
            RunnerNodeExecuter.executeNode(
                    scriptFile,
                    destinationDirectory.toString(),
                    RunnerPropertiesSerializer.serialize(properties)
            )
        } finally {
            try {
                destinationDirectory.deleteRecursivelyIfExists()
            } catch (e: Exception) {
                println("failed to delete temporary data file directory [$destinationDirectory]")
            }
        }
    }

}


