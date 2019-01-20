package com.testerum.runner_cmdline.events.execution_listeners.report_model.template

import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.common_kotlin.PathUtils
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.deleteContentsRecursivelyIfExists
import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.readText
import com.testerum.common_kotlin.writeText
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.statistics_model.Stats
import com.testerum.runner.statistics_model.events_aggregators.StatsEventsAggregator
import com.testerum.runner.statistics_model.stats_aggregator.StatsStatsAggregator
import com.testerum.runner_cmdline.dirs.RunnerDirs
import com.testerum.runner_cmdline.events.execution_listeners.json_stats.JsonStatsExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.report_model.template.custom_template.CustomTemplateExecutionListener
import com.testerum.runner_cmdline.events.execution_listeners.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.runner_cmdline.events.execution_listeners.utils.node.RunnerNodeExecuter
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.nio.file.Path as JavaPath

class ManagedReportsExecutionListener(private val managedReportsDir: JavaPath) : ExecutionListener {

    companion object {
        private val LOG = LoggerFactory.getLogger(ManagedReportsExecutionListener::class.java)

        private val LATEST_REPORT_HTML_FILE_CONTENT: String = run {
            val filePath = "/managed-reports/latest-report.html"

            val fileInputStream = ManagedReportsExecutionListener::class.java.getResourceAsStream(filePath)
                    ?: throw RuntimeException("could not load classpath resource at [$filePath]")

            fileInputStream.use {
                IOUtils.toString(it, Charsets.UTF_8)
            }
        }
    }

    private val reportsDestinationDirectory = RunnerDirs.createResultsDirectoryName(managedReportsDir)

    private val managedPrettyListener = run {
        val scriptFileName: JavaPath = RunnerDirs.getReportTemplatesDir()
                .resolve("pretty")
                .resolve("main.bundle.js")
                .toAbsolutePath().normalize()

        val properties = HashMap<String, String>()
        properties[EventListenerProperties.CustomTemplate.SCRIPT_FILE] = scriptFileName.toString()
        properties[EventListenerProperties.Pretty.DESTINATION_DIRECTORY] = reportsDestinationDirectory.resolve("pretty").toString()

        CustomTemplateExecutionListener(properties)
    }

    private val statsAggregator = StatsEventsAggregator()

    private val managedStatsListener = run {
        val scriptFileName: JavaPath = RunnerDirs.getReportTemplatesDir()
                .resolve("pretty")
                .resolve("main.bundle.js")
                .toAbsolutePath().normalize()

        val properties = HashMap<String, String>()
        properties[EventListenerProperties.CustomTemplate.SCRIPT_FILE] = scriptFileName.toString()
        properties[EventListenerProperties.JsonStats.DESTINATION_FILE_NAME] = reportsDestinationDirectory.resolve("json_stats").resolve("stats.json").toString()

        JsonStatsExecutionListener(properties)
    }

    override fun start() {
        managedPrettyListener.start()
        managedStatsListener.start()
    }

    override fun onEvent(event: RunnerEvent) {
        managedPrettyListener.onEvent(event)
        managedStatsListener.onEvent(event)
        statsAggregator.aggregate(event)
    }

    override fun stop() {
        var exception: Throwable? = null
        try {
            managedPrettyListener.stop()
            managedStatsListener.stop()
        } catch (e: Exception) {
            exception = e
        }

        // todo: extract methods
        writeLatestSymlink()
        writeLatestReportHtmlFile()

        writeJsonFullStats()
        aggregateJsonFullStats()
        writeFullStatsApp()

        exception?.let { throw it }
    }

    /** create/update "latest" report symlink */
    private fun writeLatestSymlink() {
        try {
            PathUtils.createOrUpdateSymbolicLink(
                    absoluteSymlinkPath = RunnerDirs.getLatestReportSymlink(managedReportsDir).toAbsolutePath().normalize(),
                    absoluteTarget = reportsDestinationDirectory.toAbsolutePath().normalize(),
                    symlinkRelativeTo = managedReportsDir
            )
        } catch (e: Exception) {
            LOG.warn("""failed to create/update "latest" report symlink""", e)
        }
    }

    /** create/update "latest-report.html" file */
    private fun writeLatestReportHtmlFile() {
        try {
            val latestReportFile: JavaPath = managedReportsDir.resolve("latest-report.html")

            if (shouldWriteLatestReportHtmlFile(latestReportFile)) {
                latestReportFile.writeText(LATEST_REPORT_HTML_FILE_CONTENT)
            }
        } catch (e: Exception) {
            LOG.warn("failed to write [latest-report.html] file", e)
        }
    }

    private fun shouldWriteLatestReportHtmlFile(latestReportFile: JavaPath): Boolean {
        if (latestReportFile.doesNotExist) {
            return true
        }

        // different content
        // if the content is the same, don't overwrite, because a browser might read it just then
        val actualContent = latestReportFile.readText()
        if (actualContent != LATEST_REPORT_HTML_FILE_CONTENT) {
            return true
        }

        return false
    }

    /** create/update json full stats */
    private fun writeJsonFullStats() {
        try {
            val fullStatsFile = RunnerDirs.getFullStatsFileName(reportsDestinationDirectory)
            fullStatsFile.parent?.createDirectories()
            EXECUTION_LISTENERS_OBJECT_MAPPER.writeValue(fullStatsFile.toFile(), statsAggregator.getResult())
        } catch (e: Exception) {
            LOG.warn("failed to create/update json full stats", e)
        }
    }

    private fun aggregateJsonFullStats() {
        try {
            val aggregatedStats: Stats = aggregateStats()

            // make sure the aggregatedStatisticsDir exists and is empty
            val aggregatedStatisticsDir = RunnerDirs.getAggregatedStatisticsDir(managedReportsDir)
            aggregatedStatisticsDir.deleteContentsRecursivelyIfExists()
            aggregatedStatisticsDir.createDirectories()

            val aggregatedStatisticsJsonFile = RunnerDirs.getAggregatedStatisticsJsonFile(managedReportsDir)
            EXECUTION_LISTENERS_OBJECT_MAPPER.writeValue(aggregatedStatisticsJsonFile.toFile(), aggregatedStats)
        } catch (e: Exception) {
            LOG.warn("failed to create/update json full stats", e)
        }
    }

    private fun aggregateStats(): Stats {
        val aggregator = StatsStatsAggregator()

        RunnerDirs.processExecutionDirs(managedReportsDir) { executionDir ->
            val fullStats = loadFullStats(executionDir)

            if (fullStats != null) {
                aggregator.aggregate(fullStats)
            }
        }

        return aggregator.getResult()
    }

    private fun loadFullStats(executionDir: JavaPath): Stats? {
        val fullStatsFile = RunnerDirs.getFullStatsFileName(executionDir)
        if (fullStatsFile.doesNotExist) {
            return null
        }

        return EXECUTION_LISTENERS_OBJECT_MAPPER.readValue(fullStatsFile.toFile())
    }

    private fun writeFullStatsApp() {
        val scriptFileName: JavaPath = RunnerDirs.getReportTemplatesDir()
                .resolve("stats")
                .resolve("main.bundle.js")
                .toAbsolutePath().normalize()
        val modelFile = RunnerDirs.getAggregatedStatisticsJsonFile(managedReportsDir)
        val destinationDirectory = RunnerDirs.getAggregatedStatisticsDir(managedReportsDir)

        RunnerNodeExecuter.executeNode(
                scriptFileName,
                modelFile.toString(),
                destinationDirectory.toString()
        )
    }

}
