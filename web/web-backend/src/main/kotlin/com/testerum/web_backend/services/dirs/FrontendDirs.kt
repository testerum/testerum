package com.testerum.web_backend.services.dirs

import com.testerum.settings.TesterumDirs
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

class FrontendDirs(private val testerumDirs: TesterumDirs) {

    fun getTesterumDir(): JavaPath = Paths.get(System.getProperty("user.home")).resolve(".testerum")

    fun getBasicStepsDir(): JavaPath = testerumDirs.getBasicStepsDir()

    fun getSettingsDir(): JavaPath = getTesterumDir().resolve("conf")

    fun getRecentProjectsFile(): JavaPath = getSettingsDir().resolve("recent-projects.json")

    fun getCacheDir(): JavaPath = getTesterumDir().resolve("cache")
    fun getLicensesDir(): JavaPath = getTesterumDir().resolve("licenses")

    fun getReportsDir(): JavaPath = getTesterumDir().resolve("reports")
    fun getAggregatedStatisticsDir(): JavaPath = getReportsDir().resolve("statistics")

    fun getJdbcDriversDir(): JavaPath = testerumDirs.getJdbcDriversDir()

}
