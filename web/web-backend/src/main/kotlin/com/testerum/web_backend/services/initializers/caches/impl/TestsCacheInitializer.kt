package com.testerum.web_backend.services.initializers.caches.impl

import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.settings.keys.SystemSettingKeys
import com.testerum.web_backend.services.dirs.FrontendDirs
import org.slf4j.LoggerFactory

class TestsCacheInitializer(private val frontendDirs: FrontendDirs,
                            private val testsCache: TestsCache) {

    companion object {
        private val LOG = LoggerFactory.getLogger(TestsCacheInitializer::class.java)
    }

    fun initialize() {
        val repositoryDir = frontendDirs.getRepositoryDir()
        if (repositoryDir == null) {
            LOG.info("not initializing the tests cache, because the setting [${SystemSettingKeys.REPOSITORY_DIR}] is not set")
            return
        }

        val resourcesDir = frontendDirs.getResourcesDir(repositoryDir)
        val testsDir = frontendDirs.getTestsDir(repositoryDir)

        testsCache.initialize(testsDir, resourcesDir)
    }

}
