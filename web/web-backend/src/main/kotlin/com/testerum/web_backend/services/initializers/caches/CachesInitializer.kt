package com.testerum.web_backend.services.initializers.caches

import com.testerum.web_backend.services.initializers.caches.impl.BasicStepsCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.JdbcDriversCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.LicenseCacheInitializer
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

class CachesInitializer(private val basicStepsCacheInitializer: BasicStepsCacheInitializer,
                        private val jdbcDriversCacheInitializer: JdbcDriversCacheInitializer,
                        private val licenseCacheInitializer: LicenseCacheInitializer) {

    companion object {
        private val LOG = LoggerFactory.getLogger(CachesInitializer::class.java)
    }

    fun initialize() {
        LOG.info("initializing caches...")

        val timeTakenMillis = measureTimeMillis {
            basicStepsCacheInitializer.initialize()
            jdbcDriversCacheInitializer.initialize()
            licenseCacheInitializer.initialize()
        }

        LOG.info("... done initializing caches; timeTaken=$timeTakenMillis ms")
    }

}
