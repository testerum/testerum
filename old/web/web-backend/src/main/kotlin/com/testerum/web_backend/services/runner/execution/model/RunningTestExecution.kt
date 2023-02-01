package com.testerum.web_backend.services.runner.execution.model

import com.testerum.model.runner.config.PathWithScenarioIndexes
import com.testerum.web_backend.services.runner.execution.stopper.TestExecutionStopper
import java.nio.file.Path as JavaPath

class RunningTestExecution(executionId: Long,
                           testOrDirectoryPathsToRun: List<PathWithScenarioIndexes>,
                           settings: Map<String, String>,
                           projectRootDir: JavaPath,
                           variablesEnvironment: String?,
                           val stopper: TestExecutionStopper): TestExecution(executionId, testOrDirectoryPathsToRun, settings, projectRootDir, variablesEnvironment)
