package com.testerum.web_backend.services.runner.execution.model

import com.testerum.model.infrastructure.path.Path
import com.testerum.web_backend.services.runner.execution.stopper.TestExecutionStopper
import java.nio.file.Path as JavaPath

class RunningTestExecution(executionId: Long,
                           testOrDirectoryPathsToRun: List<Path>,
                           projectRootDir: JavaPath,
                           val stopper: TestExecutionStopper): TestExecution(executionId, testOrDirectoryPathsToRun, projectRootDir)
