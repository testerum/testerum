package com.testerum.web_backend.services.initializers.caches.impl

import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.settings.keys.SystemSettingKeys
import com.testerum.web_backend.services.dirs.FrontendDirs
import org.slf4j.LoggerFactory

class FeaturesCacheInitializer(private val frontendDirs: FrontendDirs,
                               private val featuresCache: FeaturesCache) {

    companion object {
        private val LOG = LoggerFactory.getLogger(FeaturesCacheInitializer::class.java)
    }

    fun initialize() {
        val repositoryDir = frontendDirs.getRepositoryDir()
        if (repositoryDir == null) {
            LOG.info("not initializing the features cache, because the setting [${SystemSettingKeys.REPOSITORY_DIR}] is not set")
            return
        }

        val featuresDir = frontendDirs.getFeaturesDir(repositoryDir)

        featuresCache.initialize(featuresDir)
    }

}
