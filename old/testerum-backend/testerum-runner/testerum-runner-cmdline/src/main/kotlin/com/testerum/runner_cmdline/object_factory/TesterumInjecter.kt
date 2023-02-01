package com.testerum.runner_cmdline.object_factory

import com.testerum_api.testerum_steps_api.test_context.TestContext
import com.testerum_api.testerum_steps_api.test_context.TestContextAware

object TesterumInjecter {

    fun inject(destination: Any?,
               testContext: TestContext) {
        if (destination == null) {
            return
        }

        if (destination is TestContextAware) {
            destination.setTestContext(testContext)
        }
    }

}
