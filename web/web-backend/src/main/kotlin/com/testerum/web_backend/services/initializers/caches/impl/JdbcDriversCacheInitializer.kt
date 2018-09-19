package com.testerum.web_backend.services.initializers.caches.impl

import com.testerum.common_rdbms.JdbcDriversCache
import com.testerum.settings.keys.SystemSettingKeys
import com.testerum.web_backend.services.dirs.FrontendDirs
import org.slf4j.LoggerFactory

class JdbcDriversCacheInitializer(private val frontendDirs: FrontendDirs,
                                  private val jdbcDriversCache: JdbcDriversCache) {

    companion object {
        private val LOG = LoggerFactory.getLogger(JdbcDriversCacheInitializer::class.java)
    }

    fun initialize() {
        val jdbcDriversDir = frontendDirs.getJdbcDriversDir()
        if (jdbcDriversDir == null) {
            LOG.info("not initializing the JDBC drivers cache, because the setting [${SystemSettingKeys.JDBC_DRIVERS_DIR}] is not set")
            return
        }

        jdbcDriversCache.initialize(jdbcDriversDir)
    }

}
