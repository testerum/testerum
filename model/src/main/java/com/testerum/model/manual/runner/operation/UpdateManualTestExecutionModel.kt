package com.testerum.model.manual.runner.operation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.runner.ManualTestExe

data class UpdateManualTestExecutionModel @JsonCreator constructor(
        @JsonProperty("manualTestRunnerPath") val manualTestRunnerPath: Path,
        @JsonProperty("manualTestExe") val manualTest: ManualTestExe)