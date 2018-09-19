package com.testerum.common_di

import org.slf4j.LoggerFactory
import java.util.*

class ModuleFactoryContext : AutoCloseable {

    companion object {
        private val LOG = LoggerFactory.getLogger(ModuleFactoryContext::class.java)
    }

    private val shutdownHooks: MutableList<() -> Unit> = ArrayList()

    fun registerShutdownHook(hook: () -> Unit) {
        shutdownHooks += hook
    }

    fun shutdown() {
        for (shutdownHook in shutdownHooks) {
            try {
                shutdownHook()
            } catch (e: Exception) {
                LOG.error("shutdown hook failed; we will attempt to run the rest of the hooks also", e)
            }
        }
    }

    override fun close() = shutdown()

}

