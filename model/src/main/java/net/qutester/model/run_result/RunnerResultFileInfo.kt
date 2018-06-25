package net.qutester.model.run_result

import net.qutester.model.infrastructure.path.Path
import com.testerum.api.test_context.ExecutionStatus

data class RunnerResultFileInfo (
        val path: Path,
        val name: String,
        val executionResult: ExecutionStatus?,
        val durationMillis: Long?
)
