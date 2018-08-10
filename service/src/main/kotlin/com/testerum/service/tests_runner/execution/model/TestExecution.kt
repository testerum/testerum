package com.testerum.service.tests_runner.execution.model

import com.testerum.model.infrastructure.path.Path
import com.testerum.service.tests_runner.execution.stopper.TestExecutionStopper

open class TestExecution(val executionId: Long,
                         val testOrDirectoryPathsToRun: List<Path>) {

    fun toRunning(stopper: TestExecutionStopper) = RunningTestExecution(executionId, testOrDirectoryPathsToRun, stopper)

}
