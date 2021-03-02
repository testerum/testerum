package com.testerum.report_generators.reports.report_model.template

import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.common_kotlin.PathUtils
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.deleteContentsRecursivelyIfExists
import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.readText
import com.testerum.common_kotlin.writeText
import com.testerum.report_generators.dirs.ReportDirs
import com.testerum.report_generators.reports.json_stats.JsonStatsExecutionListener
import com.testerum.report_generators.reports.report_model.template.custom_template.CustomTemplateExecutionListener
import com.testerum.report_generators.reports.report_model.template.instance.ReportInstance
import com.testerum.report_generators.reports.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.statistics_model.Stats
import com.testerum.runner.statistics_model.events_aggregators.StatsEventsAggregator
import com.testerum.runner.statistics_model.stats_aggregator.StatsStatsAggregator
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.nio.file.Path as JavaPath

class ManagedReportsExecutionListener(private val managedReportsDir: JavaPath) : ExecutionListener {

    companion object {
        private val LOG = LoggerFactory.getLogger(ManagedReportsExecutionListener::class.java)

        private val AUTO_REFRESH_DASHBOARD_HTML_FILE_CONTENT: String = getClasspathResourceContent("/managed-reports/autorefresh-dashboard.html")
        private val LATEST_REPORT_HTML_FILE_CONTENT: String = getClasspathResourceContent("/managed-reports/latest-report.html")
        private val STATISTICS_HTML_FILE_CONTENT: String = getClasspathResourceContent("/managed-reports/statistics.html")

        private fun getClasspathResourceContent(location: String): String {
            val fileInputStream = ManagedReportsExecutionListener::class.java.getResourceAsStream(location)
                    ?: throw RuntimeException("could not load classpath resource at [$location]")

            fileInputStream.use {
                return IOUtils.toString(it, Charsets.UTF_8)
            }
        }
    }

    private val reportsDestinationDirectory = ReportDirs.createResultsDirectoryName(managedReportsDir)

    private val managedPrettyListener = run {
        val templateDirectory: JavaPath = ReportDirs.getReportTemplatesDir()
                .resolve("pretty")
                .toAbsolutePath().normalize()

        val properties = HashMap<String, String>()
        properties[EventListenerProperties.CustomTemplate.TEMPLATE_DIRECTORY] = templateDirectory.toString()
        properties[EventListenerProperties.Pretty.DESTINATION_DIRECTORY] = reportsDestinationDirectory.resolve("pretty").toString()

        CustomTemplateExecutionListener(properties)
    }

    private val statsAggregator = StatsEventsAggregator()

    private val managedStatsListener = run {
        val templateDirectory: JavaPath = ReportDirs.getReportTemplatesDir()
                .resolve("pretty")
                .toAbsolutePath().normalize()

        val properties = HashMap<String, String>()
        properties[EventListenerProperties.CustomTemplate.TEMPLATE_DIRECTORY] = templateDirectory.toString()
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

        writeStatisticsHtmlFile()
        writeLatestReportHtmlFile()
        writeLatestSymlink()
        writeAutoRefreshDashboardHtmlFile()

        writeJsonFullStats()
        aggregateJsonFullStats()
        writeFullStatsApp()

        exception?.let { throw it }
    }

    /** create/update "latest" report symlink */
    private fun writeLatestSymlink() {
        try {
            PathUtils.createOrUpdateSymbolicLink(
                    absoluteSymlinkPath = ReportDirs.getLatestReportSymlink(managedReportsDir).toAbsolutePath().normalize(),
                    absoluteTarget = reportsDestinationDirectory.toAbsolutePath().normalize(),
                    symlinkRelativeTo = managedReportsDir
            )
        } catch (e: Exception) {
            LOG.warn("""failed to create/update "latest" report symlink""", e)
        }
    }

    /** create/update "statistics.html" report symlink */
    private fun writeStatisticsSymlink() {
        try {
            PathUtils.createOrUpdateSymbolicLink(
                    absoluteSymlinkPath = managedReportsDir.resolve("statistics.html").toAbsolutePath().normalize(),
                    absoluteTarget = ReportDirs.getAggregatedStatisticsDir(managedReportsDir).resolve("index.html").toAbsolutePath().normalize(),
                    symlinkRelativeTo = managedReportsDir
            )
        } catch (e: Exception) {
            LOG.warn("""failed to create/update "statistics.html" symlink""", e)
        }
    }

    /** create/update "autorefresh-dashboard.html" file */
    private fun writeAutoRefreshDashboardHtmlFile() {
        try {
            val autoRefreshDashboardFile: JavaPath = managedReportsDir.resolve("autorefresh-dashboard.html")

            if (shouldWriteAutoRefreshDashboardHtmlFile(autoRefreshDashboardFile)) {
                autoRefreshDashboardFile.writeText(AUTO_REFRESH_DASHBOARD_HTML_FILE_CONTENT)
            }
        } catch (e: Exception) {
            LOG.warn("failed to write [autorefresh-dashboard.html] file", e)
        }
    }

    private fun shouldWriteAutoRefreshDashboardHtmlFile(autoRefreshDashboardFile: JavaPath): Boolean {
        if (autoRefreshDashboardFile.doesNotExist) {
            return true
        }

        // different content
        // if the content is the same, don't overwrite, because a browser might read it just then
        val actualContent = autoRefreshDashboardFile.readText()
        if (actualContent != AUTO_REFRESH_DASHBOARD_HTML_FILE_CONTENT) {
            return true
        }

        return false
    }

    /** create/update "latest report" file */
    private fun writeLatestReportHtmlFile() {
        try {
            val latestReportFile: JavaPath = managedReportsDir.resolve("latest-report.html")

            latestReportFile.writeText(LATEST_REPORT_HTML_FILE_CONTENT)
        } catch (e: Exception) {
            LOG.warn("failed to write [latest-report.html] file", e)
        }
    }

    /** create/update "statistics.html" report symlink */
    private fun writeStatisticsHtmlFile() {
        try {
            val statisticsFile: JavaPath = managedReportsDir.resolve("statistics.html")

            statisticsFile.writeText(STATISTICS_HTML_FILE_CONTENT)
        } catch (e: Exception) {
            LOG.warn("failed to write [statistics.html] file", e)
        }
    }

    /** create/update json full stats */
    private fun writeJsonFullStats() {
        try {
            val fullStatsFile = ReportDirs.getFullStatsFileName(reportsDestinationDirectory)
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
            val aggregatedStatisticsDir = ReportDirs.getAggregatedStatisticsDir(managedReportsDir)
            aggregatedStatisticsDir.deleteContentsRecursivelyIfExists()
            aggregatedStatisticsDir.createDirectories()

            val aggregatedStatisticsJsonFile = ReportDirs.getAggregatedStatisticsJsonFile(managedReportsDir)
            EXECUTION_LISTENERS_OBJECT_MAPPER.writeValue(aggregatedStatisticsJsonFile.toFile(), aggregatedStats)
        } catch (e: Exception) {
            LOG.warn("failed to create/update json full stats", e)
        }
    }

    private fun aggregateStats(): Stats {
        val aggregator = StatsStatsAggregator()

        ReportDirs.processExecutionDirs(managedReportsDir) { executionDir ->
            val fullStats = loadFullStats(executionDir)

            if (fullStats != null) {
                aggregator.aggregate(fullStats)
            }
        }

        return aggregator.getResult()
    }

    private fun loadFullStats(executionDir: JavaPath): Stats? {
        val fullStatsFile = ReportDirs.getFullStatsFileName(executionDir)
        if (fullStatsFile.doesNotExist) {
            return null
        }

        return EXECUTION_LISTENERS_OBJECT_MAPPER.readValue(fullStatsFile.toFile())
    }

    private fun writeFullStatsApp() {
        val dataModelPatternToReplace = Regex("<!--### START: testerumRunnerStatisticsModel ### -->[\\s\\S]*<!--### END: testerumRunnerStatisticsModel ### -->")
        val dataModelAsString = EXECUTION_LISTENERS_OBJECT_MAPPER.writeValueAsString(aggregateStats())
        val dataModelPatternReplacement = "<script type='text/javascript'>\n    window.testerumRunnerStatisticsModel = ${dataModelAsString};\n  </script>"

        val statsReportTemplate: JavaPath = ReportDirs.getReportTemplatesDir()
            .resolve("stats")
            .toAbsolutePath().normalize()
        val destinationDirectory = ReportDirs.getAggregatedStatisticsDir(managedReportsDir)

        ReportInstance.create(
            templateDirectory = statsReportTemplate,
            destinationDirectory = destinationDirectory,
            dataModelPatternToReplace = dataModelPatternToReplace,
            dataModelPatternReplacement = dataModelPatternReplacement
        )
    }
}
