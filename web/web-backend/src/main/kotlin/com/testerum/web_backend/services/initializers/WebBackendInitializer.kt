package com.testerum.web_backend.services.initializers

import com.testerum.web_backend.services.initializers.caches.CachesInitializer
import com.testerum.web_backend.services.initializers.info_logging.InfoLoggerInitializer
import com.testerum.web_backend.services.initializers.settings.SettingsManagerInitializer

class WebBackendInitializer(private val settingsManagerInitializer: SettingsManagerInitializer,
                            private val cachesInitializer: CachesInitializer,
                            private val infoLoggerInitializer: InfoLoggerInitializer) {

    fun initialize() {
        settingsManagerInitializer.initialize()
        cachesInitializer.initialize()

        infoLoggerInitializer.initialize()
    }

}