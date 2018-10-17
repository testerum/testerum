package com.testerum.web_backend.services.initializers.caches

import com.testerum.web_backend.services.initializers.caches.impl.*
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

class CachesInitializer(private val stepsCacheInitializer: StepsCacheInitializer,
                        private val testsCacheInitializer: TestsCacheInitializer,
                        private val featuresCacheInitializer: FeaturesCacheInitializer,
                        private val jdbcDriversCacheInitializer: JdbcDriversCacheInitializer,
                        private val licenseCacheInitializer: LicenseCacheInitializer) {

    companion object {
        private val LOG = LoggerFactory.getLogger(CachesInitializer::class.java)
    }

    fun initialize() {
        LOG.info("initializing caches...")

        val timeTakenMillis = measureTimeMillis {
            stepsCacheInitializer.initialize()
            testsCacheInitializer.initialize()
            featuresCacheInitializer.initialize()
            jdbcDriversCacheInitializer.initialize()
            licenseCacheInitializer.initialize()
        }

        LOG.info("... done initializing caches; timeTaken=$timeTakenMillis ms")
    }

}
