package com.testerum.web_backend.services.initializers.caches

import com.testerum.web_backend.services.initializers.caches.impl.FeaturesCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.JdbcDriversCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.StepsCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.TestsCacheInitializer
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

class CachesInitializer(private val stepsCacheInitializer: StepsCacheInitializer,
                        private val testsCacheInitializer: TestsCacheInitializer,
                        private val featuresCacheInitializer: FeaturesCacheInitializer,
                        private val jdbcDriversCacheInitializer: JdbcDriversCacheInitializer) {

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
        }

        LOG.info("... done initializing caches; timeTaken=$timeTakenMillis ms")
    }

}
