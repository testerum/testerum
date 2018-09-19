package com.testerum.web_backend.services.runner.execution.model

import com.testerum.model.infrastructure.path.Path
import com.testerum.web_backend.services.runner.execution.stopper.TestExecutionStopper

open class TestExecution(val executionId: Long,
                         val testOrDirectoryPathsToRun: List<Path>) {

    fun toRunning(stopper: TestExecutionStopper) = RunningTestExecution(executionId, testOrDirectoryPathsToRun, stopper)

}
