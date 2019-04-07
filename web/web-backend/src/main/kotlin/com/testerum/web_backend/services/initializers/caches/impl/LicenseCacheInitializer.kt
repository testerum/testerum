package com.testerum.web_backend.services.initializers.caches.impl

import com.testerum.cloud_client.licenses.cache.LicensesCache
import com.testerum.web_backend.services.dirs.FrontendDirs

class LicenseCacheInitializer(private val frontendDirs: FrontendDirs,
                              private val licensesCache: LicensesCache) {

    fun initialize() {
        val licensesDir = frontendDirs.getLicensesDir()

        licensesCache.initialize(licensesDir)
    }

}
