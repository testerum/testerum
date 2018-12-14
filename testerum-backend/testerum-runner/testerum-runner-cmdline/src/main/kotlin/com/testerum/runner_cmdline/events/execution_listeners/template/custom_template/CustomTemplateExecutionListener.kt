package com.testerum.runner_cmdline.events.execution_listeners.template.custom_template

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.testerum.common_jdk.OsUtils
import com.testerum.common_jdk.toStringWithStacktrace
import com.testerum.common_kotlin.deleteOnExit
import com.testerum.common_kotlin.doesNotExist
import com.testerum.runner.cmdline.EventListenerProperties
import com.testerum.runner.exit_code.ExitCode
import com.testerum.runner.report_model.ReportSuite
import com.testerum.runner_cmdline.dirs.RunnerDirs
import com.testerum.runner_cmdline.events.execution_listeners.template.BaseReportModelExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.println
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.stream.LogOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

class CustomTemplateExecutionListener(private val properties: Map<String, String>) : BaseReportModelExecutionListener() {

    companion object {
        private val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            disable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

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

    override fun handleReportModel(reportSuite: ReportSuite) {
        // write data file
        val reportDataFile = Files.createTempFile("testerum.runner-report-data-", ".json").toAbsolutePath().normalize()
        reportDataFile.deleteOnExit()
        OBJECT_MAPPER.writeValue(reportDataFile.toFile(), reportSuite)

        var commandLine: List<String> = emptyList()
        try {
            // serialize properties to JSON
            val jsonProperties: String = OBJECT_MAPPER.writeValueAsString(properties)

            // execute node
            val scriptFileName = scriptFile.fileName?.toString()
            commandLine = createCommandLine(reportDataFile, jsonProperties)
            val processExecutor = ProcessExecutor()
                    .command(commandLine)
                    .redirectOutput(
                            object : LogOutputStream() {
                                override fun processLine(line: String) {
                                    ConsoleOutputCapturer.getOriginalTextWriter().println("[$scriptFileName] $line")
                                }
                            }
                    )
            val startTime = System.currentTimeMillis()
            val processResult: ProcessResult = processExecutor.execute()
            ConsoleOutputCapturer.getOriginalTextWriter().println("execution took ${System.currentTimeMillis() - startTime} ms")

            if (processResult.exitValue == ExitCode.RUNNER_FAILED.code) {
                ConsoleOutputCapturer.getOriginalTextWriter().println(
                        """|report process exited with code ${processResult.exitValue}
                           |commandLine = $commandLine
                           |output = [${processResult.outputUTF8()}]
                        """.trimMargin()
                )
            }
        } catch (e: Exception) {
            ConsoleOutputCapturer.getOriginalTextWriter().println(
                    """|failed to start the report process
                       |commandLine = $commandLine
                       |exception = ${e.toStringWithStacktrace()}
                    """.trimMargin()
            )
        } finally {
            try {
                Files.delete(reportDataFile)
            } catch (e: Exception) {
                println("failed to delete reportDataFile [$reportDataFile]")
            }
        }
    }

    private fun createCommandLine(reportDataFile: JavaPath,
                                  jsonProperties: String): List<String> {
        val commandLine = mutableListOf<String>()

        commandLine += getNodeBinaryPath().toString()

        commandLine += scriptFile.toString()
        commandLine += reportDataFile.toString()
        commandLine += jsonProperties

        return commandLine
    }

    private fun getNodeBinaryPath(): JavaPath {
        val nodeDir: JavaPath = RunnerDirs.getRunnerNodeDir()

        return if (OsUtils.IS_WINDOWS) {
            nodeDir.resolve("node.exe")
        } else {
            nodeDir.resolve("node")
        }
    }


}


