package com.testerum.web_backend.services.runner.execution.model

import com.testerum.model.runner.config.PathWithScenarioIndexes
import com.testerum.web_backend.services.runner.execution.stopper.TestExecutionStopper
import java.nio.file.Path as JavaPath

open class TestExecution(val executionId: Long,
                         val testOrDirectoryPathsToRun: List<PathWithScenarioIndexes>,
                         val settings: Map<String, String>,
                         val projectRootDir: JavaPath,
                         val variablesEnvironment: String?) {

    fun toRunning(stopper: TestExecutionStopper) = RunningTestExecution(executionId, testOrDirectoryPathsToRun, settings, projectRootDir, variablesEnvironment, stopper)

}
