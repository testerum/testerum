package com.testerum.model.run_result

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.model.infrastructure.path.Path

data class RunnerResultFileInfo(val path: Path,
                                val name: String,
                                val url: String,
                                val executionResult: ExecutionStatus?,
                                val durationMillis: Long?)
