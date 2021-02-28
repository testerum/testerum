package com.testerum.runner_cmdline.test_context

import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.TestContext

class TestContextImpl(override val stepsClassLoader: ClassLoader) : TestContext {

    override var testName: String? = null
    override var testPath : String? = null

    override var testStatus: ExecutionStatus? = null

}
