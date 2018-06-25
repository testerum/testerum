package com.testerum.runner.object_factory

import com.testerum.api.test_context.TestContext
import com.testerum.runner.glue_object_factory.GlueObjectFactory

class EnhancedGlueObjectFactory(private val wrapped: GlueObjectFactory,
                                private val testContext: TestContext) : GlueObjectFactory by wrapped {

    override fun <T> getInstance(glueClass: Class<out T>): T {
        val instance = wrapped.getInstance(glueClass)

        TesterumInjecter.inject(instance, testContext)

        return instance
    }

}