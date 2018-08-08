package com.testerum.runner_cmdline.test_context

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.api.test_context.TestContext

class TestContextImpl(override val stepsClassLoader: ClassLoader) : TestContext {

    override var testStatus: ExecutionStatus = ExecutionStatus.PASSED

}