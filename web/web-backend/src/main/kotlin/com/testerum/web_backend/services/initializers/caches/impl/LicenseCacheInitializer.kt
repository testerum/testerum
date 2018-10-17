package com.testerum.web_backend.services.initializers.caches.impl

import com.testerum.licenses.cache.LicensesCache
import com.testerum.web_backend.services.dirs.FrontendDirs
import org.slf4j.LoggerFactory

class LicenseCacheInitializer(private val frontendDirs: FrontendDirs,
                              private val licensesCache: LicensesCache) {

    companion object {
        private val LOG = LoggerFactory.getLogger(LicenseCacheInitializer::class.java)
    }

    fun initialize() {
        val licensesDir = frontendDirs.getLicensesDir()

        licensesCache.initialize(licensesDir)
    }

}