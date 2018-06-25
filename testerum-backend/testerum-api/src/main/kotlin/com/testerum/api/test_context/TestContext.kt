package com.testerum.api.test_context

import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.api.test_context.test_vars.TestVariables

interface TestContext {

    val testVariables: TestVariables
    val settingsManager: SettingsManager
    val testStatus: ExecutionStatus
    val stepsClassLoader: ClassLoader
    val logger: TesterumLogger

}
