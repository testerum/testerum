package com.testerum.web_backend.services.runner.execution.model

import com.testerum.model.infrastructure.path.Path
import com.testerum.web_backend.services.runner.execution.stopper.TestExecutionStopper

class RunningTestExecution(executionId: Long,
                           testOrDirectoryPathsToRun: List<Path>,
                           val stopper: TestExecutionStopper): TestExecution(executionId, testOrDirectoryPathsToRun)
