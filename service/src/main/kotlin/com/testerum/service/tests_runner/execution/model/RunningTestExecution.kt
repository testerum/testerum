package com.testerum.service.tests_runner.execution.model

import com.testerum.model.infrastructure.path.Path
import com.testerum.service.tests_runner.execution.stopper.TestExecutionStopper

class RunningTestExecution(executionId: Long,
                           testOrDirectoryPathsToRun: List<Path>,
                           val stopper: TestExecutionStopper): TestExecution(executionId, testOrDirectoryPathsToRun)
