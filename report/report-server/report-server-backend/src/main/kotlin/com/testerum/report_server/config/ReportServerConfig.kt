package com.testerum.report_server.config

import com.testerum.runner.events.model.ConfigurationEvent
import java.nio.file.Path
import java.nio.file.Paths

object ReportServerConfig {
    const val ENVIRONMENT_PROJECT_ID_SEPARATOR = "_ID_"

    fun getReportsRootDirectory(): Path = Paths.get(System.getProperty("user.home")).resolve(".testerum").resolve("reports")

    fun getReportPath(configEvent: ConfigurationEvent): Path {
        val rootReportsDirectory = getReportsRootDirectory()
        val executedEnvironment = configEvent.variablesEnvironment ?: "default-environment"
        val projectIdentifier = "$executedEnvironment$ENVIRONMENT_PROJECT_ID_SEPARATOR${configEvent.projectId}"

        return rootReportsDirectory.resolve(configEvent.projectName).resolve(projectIdentifier)
    }
}
