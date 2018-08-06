package com.testerum.model.manual.runner.operation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.runner.ManualTestsRunner

data class UpdateManualTestsRunnerModel @JsonCreator constructor(
        @JsonProperty("oldPath") val oldPath: Path,
        @JsonProperty("manualTestsRunner") val manualTestsRunner: ManualTestsRunner)