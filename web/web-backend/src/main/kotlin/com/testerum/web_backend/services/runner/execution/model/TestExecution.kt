package com.testerum.web_backend.services.runner.execution.model

import com.testerum.model.infrastructure.path.Path
import com.testerum.web_backend.services.runner.execution.stopper.TestExecutionStopper
import java.nio.file.Path as JavaPath

open class TestExecution(val executionId: Long,
                         val testOrDirectoryPathsToRun: List<Path>,
                         val projectRootDir: JavaPath) {

    fun toRunning(stopper: TestExecutionStopper) = RunningTestExecution(executionId, testOrDirectoryPathsToRun, projectRootDir ,stopper)

}
