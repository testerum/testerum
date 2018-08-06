package com.testerum.runner_cmdline.object_factory

import com.testerum.api.test_context.TestContext
import com.testerum.api.test_context.TestContextAware
import kotlin.reflect.full.safeCast

object TesterumInjecter {

    fun inject(destination: Any?,
               testContext: TestContext) {
        if (destination == null) {
            return
        }

        TestContextAware::class.safeCast(destination)?.setTestContext(testContext)
    }

}