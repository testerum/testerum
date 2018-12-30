package com.testerum.web_backend.services.dirs

import com.testerum.api.test_context.settings.model.resolvedValueAsPath
import com.testerum.settings.SettingsManager
import com.testerum.settings.hasValue
import com.testerum.settings.keys.SystemSettingKeys
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

class FrontendDirs(private val settingsManager: SettingsManager) {

    fun getTesterumDir(): JavaPath = Paths.get(System.getProperty("user.home")).resolve(".testerum")

    fun getBasicStepsDir(): JavaPath? = settingsManager.getSetting(SystemSettingKeys.BUILT_IN_BASIC_STEPS_DIR)?.resolvedValueAsPath

    fun getSettingsDir(): JavaPath = getTesterumDir().resolve("conf")

    fun getCacheDir(): JavaPath = getTesterumDir().resolve("cache")
    fun getLicensesDir(): JavaPath = getTesterumDir().resolve("licenses")

    fun getReportsDir(): JavaPath = getTesterumDir().resolve("reports")
    fun getLatestReportSymlink(): JavaPath = getReportsDir().resolve("latest")
    fun getReportsPrettyDir(executionDir: JavaPath): JavaPath = executionDir.resolve("pretty")
    fun getReportsStatsFileName(executionDir: JavaPath): JavaPath = executionDir.resolve("json_stats").resolve("stats.json") // todo: remove duplication between this method and RunnerResultFileService.loadStatistics()

    fun getRequiredResourcesDir(): JavaPath = getResourcesDir(getRequiredRepositoryDir())
    fun getOptionalResourcesDir(): JavaPath? = getRepositoryDir()?.resolve("resources")
    fun getResourcesDir(repositoryDir: JavaPath): JavaPath = repositoryDir.resolve("resources")

    fun getRequiredComposedStepsDir(): JavaPath = getComposedStepsDir(getRequiredRepositoryDir())
    fun getOptionalComposedStepsDir(): JavaPath? = getRepositoryDir()?.resolve("composed_steps")
    fun getComposedStepsDir(repositoryDir: JavaPath): JavaPath = repositoryDir.resolve("composed_steps")

    fun getRequiredFeaturesDir(): JavaPath = getFeaturesDir(getRequiredRepositoryDir())
    fun getOptionalFeaturesDir(): JavaPath? = getRepositoryDir()?.resolve("features")
    fun getFeaturesDir(repositoryDir: JavaPath): JavaPath = repositoryDir.resolve("features")

    fun getRequiredTestsDir(): JavaPath = getRequiredFeaturesDir()
    fun getOptionalTestsDir(): JavaPath? = getOptionalFeaturesDir()
    fun getTestsDir(repositoryDir: JavaPath): JavaPath = getFeaturesDir(repositoryDir)

    fun getRequiredManualTestsDir(): JavaPath = getManualTestsDir(getRequiredRepositoryDir())
    fun getOptionalManualTestsDir(): JavaPath? = getRepositoryDir()?.resolve("manual_tests")
    fun getManualTestsDir(repositoryDir: JavaPath): JavaPath = repositoryDir.resolve("manual_tests")

    fun getRequiredVariablesDir(): JavaPath = getVariablesDir(getRequiredRepositoryDir())
    fun getOptionalVariablesDir(): JavaPath? = getRepositoryDir()?.resolve("variables")
    fun getVariablesDir(repositoryDir: JavaPath): JavaPath = repositoryDir.resolve("variables")

    fun getRepositoryDir(): JavaPath? {
        if (!settingsManager.hasValue(SystemSettingKeys.REPOSITORY_DIR)) {
            return null
        }

        return settingsManager.getSetting(SystemSettingKeys.REPOSITORY_DIR)?.resolvedValueAsPath
    }

    fun getRequiredRepositoryDir(): JavaPath {
        return getRepositoryDir()
                ?: throw IllegalStateException("the setting [${SystemSettingKeys.REPOSITORY_DIR}] is not set")
    }

    fun getJdbcDriversDir(): JavaPath? {
        if (!settingsManager.hasValue(SystemSettingKeys.JDBC_DRIVERS_DIR)) {
            return null
        }

        return settingsManager.getSetting(SystemSettingKeys.JDBC_DRIVERS_DIR)?.resolvedValueAsPath
    }

}
