package com.testerum.web_backend.config

import com.testerum.common_profiles.TesterumProfile
import com.testerum.common_profiles.TesterumProfileFinder
import com.testerum.web_backend.config.impl.DevTesterumWebBackendConfig
import com.testerum.web_backend.config.impl.ProdTesterumWebBackendConfig

object TesterumWebBackendConfigFactory {

    fun createTesterumWebBackendConfig(): TesterumWebBackendConfig {
        return when (TesterumProfileFinder.currentProfile) {
            TesterumProfile.DEV  -> DevTesterumWebBackendConfig
            TesterumProfile.PROD -> ProdTesterumWebBackendConfig
        }
    }

}
