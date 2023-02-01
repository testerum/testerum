package com.testerum.web_backend.services.dirs

import com.testerum.settings.TesterumDirs
import java.nio.file.Path as JavaPath

class FrontendDirs(private val testerumDirs: TesterumDirs) {

    fun getBasicStepsDir(): JavaPath = testerumDirs.getBasicStepsDir()

    fun getJdbcDriversDir(): JavaPath = testerumDirs.getJdbcDriversDir()

    fun getSeleniumDriversDir(): JavaPath = testerumDirs.getSeleniumDriversDir()

    fun getTesterumDir(): JavaPath = testerumDirs.getTesterumDir()

    fun getRunConfigsDir(): JavaPath = getTesterumDir().resolve("run-configs")

    fun getFsNotifierBinariesDir(): JavaPath = testerumDirs.getFsNotifierBinariesDir()

    fun getSettingsDir(): JavaPath = getTesterumDir().resolve("conf")

    fun getRecentProjectsFile(): JavaPath = getSettingsDir().resolve("recent-projects.json")

    fun getFileLocalVariablesFile(): JavaPath = getSettingsDir().resolve("variables.json")

    fun getLicensesDir(): JavaPath = getTesterumDir().resolve("licenses")

    fun getReportsDir(projectId: String): JavaPath = getTesterumDir().resolve("reports").resolve(projectId)
    fun getAggregatedStatisticsDir(projectId: String): JavaPath = getReportsDir(projectId).resolve("statistics")

}
