package com.testerum.runner_cmdline.object_factory

import com.testerum_api.testerum_steps_api.test_context.TestContext
import com.testerum.runner.glue_object_factory.GlueObjectFactory

class EnhancedGlueObjectFactory(private val wrapped: GlueObjectFactory,
                                private val testContext: TestContext) : GlueObjectFactory by wrapped {

    override fun <T> getInstance(glueClass: Class<out T>): T {
        val instance = wrapped.getInstance(glueClass)

        TesterumInjecter.inject(instance, testContext)

        return instance
    }

}
