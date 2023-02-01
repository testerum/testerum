package com.testerum.runner_cmdline.object_factory

import com.testerum_api.testerum_steps_api.test_context.TestContext
import com.testerum.runner.glue_object_factory.GlueObjectFactory
import java.util.*

object GlueObjectFactoryFinder {

    fun findGlueObjectFactory(testContext: TestContext): EnhancedGlueObjectFactory {
        val factories: List<GlueObjectFactory> = findFactories()

        val factory: GlueObjectFactory = chooseFactory(factories)

        TesterumInjecter.inject(factory, testContext)

        return EnhancedGlueObjectFactory(factory, testContext)
    }

    private fun chooseFactory(factories: List<GlueObjectFactory>): GlueObjectFactory
            = when {
                factories.isEmpty() -> DefaultGlueObjectFactory()
                factories.size == 1 -> factories[0]
                else                -> throw RuntimeException(multipleFactoriesErrorMessage(factories))
            }

    private fun findFactories(): List<GlueObjectFactory>
            = ServiceLoader.load(GlueObjectFactory::class.java)
                           .toList()

    private fun multipleFactoriesErrorMessage(factories: List<GlueObjectFactory>)
            = "Found multiple GlueObjectFactory implementations: ${factories.map { it.javaClass.name }}." +
              "Please include a single dependency-injection module on the classpath."

}
