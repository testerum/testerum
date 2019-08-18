package com.testerum_api.testerum_steps_api.services

import com.testerum_api.testerum_steps_api.test_context.TestContext
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.settings.RunnerSettingsManager
import com.testerum_api.testerum_steps_api.test_context.settings.RunnerTesterumDirs
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables
import java.util.concurrent.ConcurrentHashMap

object TesterumServiceLocator {

    private val services = ConcurrentHashMap<Class<*>, Any>()

    @JvmStatic
    fun getTestContext(): TestContext = getService(TestContext::class.java)

    @JvmStatic
    fun getTestVariables(): TestVariables = getService(TestVariables::class.java)

    @JvmStatic
    fun getTesterumDirs(): RunnerTesterumDirs = getService(RunnerTesterumDirs::class.java)

    @JvmStatic
    fun getSettingsManager(): RunnerSettingsManager = getService(RunnerSettingsManager::class.java)

    @JvmStatic
    fun getTesterumLogger(): TesterumLogger = getService(TesterumLogger::class.java)

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <S : TesterumService> getService(serviceClass: Class<S>): S {
        return services[serviceClass] as? S
                ?: throw serviceNotFoundException(serviceClass)
    }

    @JvmStatic
    @Deprecated(message = "this method is internal to the Runner and should not be used by step libraries")
    fun <S : TesterumService> registerService(serviceClass: Class<S>, service: S) {
        services[serviceClass] = service
    }

    private fun serviceNotFoundException(serviceClass: Class<*>)
            = TesterumServiceNotFoundException("${serviceClass.simpleName} has not yet been registered with the ${TesterumServiceLocator.javaClass.simpleName}")

}
