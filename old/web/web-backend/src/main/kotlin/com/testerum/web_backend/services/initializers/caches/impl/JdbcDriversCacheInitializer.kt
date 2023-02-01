package com.testerum.web_backend.services.initializers.caches.impl

import com.testerum.common_rdbms.JdbcDriversCache
import com.testerum.web_backend.services.dirs.FrontendDirs

class JdbcDriversCacheInitializer(private val frontendDirs: FrontendDirs,
                                  private val jdbcDriversCache: JdbcDriversCache) {

    fun initialize() {
        val jdbcDriversDir = frontendDirs.getJdbcDriversDir()

        jdbcDriversCache.initialize(jdbcDriversDir)
    }

}
