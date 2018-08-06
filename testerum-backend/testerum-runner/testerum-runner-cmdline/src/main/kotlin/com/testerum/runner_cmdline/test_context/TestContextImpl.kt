package com.testerum.runner_cmdline.test_context

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.api.test_context.TestContext
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.runner_cmdline.runner_tree.vars_context.TestVariablesImpl

class TestContextImpl(testVariablesImpl: TestVariablesImpl,
                      override val settingsManager: SettingsManager,
                      override val stepsClassLoader: ClassLoader,
                      override val logger: TesterumLogger) : TestContext {

    override val testVariables: TestVariables = testVariablesImpl

    override var testStatus: ExecutionStatus = ExecutionStatus.PASSED

}