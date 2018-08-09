package com.testerum.api.services

import com.testerum.api.test_context.TestContext
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.api.test_context.test_vars.TestVariables

object TesterumServiceLocator {

    private val services = mutableMapOf<Class<*>, Any>()

    fun getTestContext(): TestContext = getService(TestContext::class.java)
    fun getTestVariables(): TestVariables = getService(TestVariables::class.java)
    fun getSettingsManager(): SettingsManager = getService(SettingsManager::class.java)
    fun getTesterumLogger(): TesterumLogger = getService(TesterumLogger::class.java)

    @Suppress("UNCHECKED_CAST")
    fun <S : TesterumService> getService(serviceClass: Class<S>): S {
        return services[serviceClass] as? S
                ?: throw serviceNotFoundException(serviceClass)
    }

    @Deprecated(message = "this method is internal to the Runner and should not be used by step libraries")
    fun <S : TesterumService> registerService(serviceClass: Class<S>, service: S) {
        services[serviceClass] = service
    }

    private fun serviceNotFoundException(serviceClass: Class<*>)
            = TesterumServiceNotFoundException("${serviceClass.simpleName} has not yet been registered with the ${TesterumServiceLocator.javaClass.simpleName}")

}
