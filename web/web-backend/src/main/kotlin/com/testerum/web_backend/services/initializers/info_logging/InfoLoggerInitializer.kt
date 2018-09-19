package com.testerum.web_backend.services.initializers.info_logging

import com.testerum.settings.SettingsManager
import com.testerum.settings.getResolvedSettingValues
import com.testerum.web_backend.services.dirs.FrontendDirs
import org.slf4j.LoggerFactory

class InfoLoggerInitializer(private val settingsManager: SettingsManager,
                            private val frontendDirs: FrontendDirs) {

    companion object {
        private val LOG = LoggerFactory.getLogger(InfoLoggerInitializer::class.java)
    }

    fun initialize() {
        logSettings()
        logDirs()
    }

    private fun logSettings() {
        LOG.info("Settings:")
        LOG.info("---------")

        InfoLoggerUtils.logMap(LOG, settingsManager.getResolvedSettingValues())

        LOG.info("")
    }

    private fun logDirs() {
        LOG.info("Dirs:")
        LOG.info("-----")

        val infoMap = mapOf<String, Any?>(
                "testerumDir"      to frontendDirs.getTesterumDir(),
                "settingsDir"      to frontendDirs.getSettingsDir(),
                "cacheDir"         to frontendDirs.getCacheDir(),
                "basicStepsDir"    to frontendDirs.getBasicStepsDir(),
                "composedStepsDir" to frontendDirs.getOptionalComposedStepsDir(),
                "featuresDir"      to frontendDirs.getOptionalFeaturesDir(),
                "testsDir"         to frontendDirs.getOptionalTestsDir(),
                "resourcesDir"     to frontendDirs.getOptionalResourcesDir(),
                "variablesDir"     to frontendDirs.getOptionalVariablesDir()
        )

        InfoLoggerUtils.logMap(LOG, infoMap)

        LOG.info("")
    }

}
