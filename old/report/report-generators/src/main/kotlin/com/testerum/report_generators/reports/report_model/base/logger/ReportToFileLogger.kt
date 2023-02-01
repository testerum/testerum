package com.testerum.report_generators.reports.report_model.base.logger

import com.testerum.common_kotlin.createDirectories
import com.testerum.report_generators.reports.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.runner.report_model.ReportLog
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.time.format.DateTimeFormatter
import java.nio.file.Path as JavaPath

class ReportToFileLogger(val textFilePath: JavaPath,
                         val modelFilePath: JavaPath) : AutoCloseable {

    companion object {
        private val LOG = LoggerFactory.getLogger(ReportToFileLogger::class.java)

        private val TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        private val MODEL_FILE_PREFIX    = "receiveModel(["
        private val MODEL_FILE_SEPARATOR = ","
        private val MODEL_FILE_POSTFIX   = "]);"
    }

    init {
        textFilePath.parent?.createDirectories()
        modelFilePath.parent?.createDirectories()
    }

    private val textFile = Files.newBufferedWriter(textFilePath)
    private val modelFile = Files.newBufferedWriter(modelFilePath)

    init {
        modelFile.write(MODEL_FILE_PREFIX)
    }

    fun log(log: ReportLog) {
        logToTextFile(log)
        logToModelFile(log)
    }

    private fun logToTextFile(logEvent: ReportLog) {
        val timestamp = TIMESTAMP_FORMATTER.format(logEvent.time)
        val logLevel = logEvent.logLevel.formatForLogging
        val message = logEvent.message
        val exceptionWithStackTrace = logEvent.exceptionDetail?.asDetailedString

        textFile.write(
                buildString {
                    append(timestamp).append("  ").append(logLevel).append(" ").append(message)
                    if (exceptionWithStackTrace != null) {
                        append("; exception:\n").append(exceptionWithStackTrace)
                    }
                    append('\n')
                }
        )
    }

    private fun logToModelFile(logEvent: ReportLog) {
        modelFile.write(EXECUTION_LISTENERS_OBJECT_MAPPER.writeValueAsString(logEvent))
        modelFile.write(MODEL_FILE_SEPARATOR)
    }

    override fun close() {
        modelFile.write(MODEL_FILE_POSTFIX)

        try {
            textFile.close()
        } catch (ignore: Exception) {
            LOG.warn("failed to close text file at [$textFilePath]")
        }
        try {
            modelFile.close()
        } catch (ignore: Exception) {
            LOG.warn("failed to close model file at [$modelFilePath]")
        }
    }
}
