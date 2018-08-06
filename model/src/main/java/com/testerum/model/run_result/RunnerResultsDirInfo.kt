package com.testerum.model.run_result

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RunnerResultsDirInfo @JsonCreator constructor(
        @JsonProperty("directoryName") val directoryName: String,
        @JsonProperty("runnerResultFilesInfo") val runnerResultFilesInfo: List<RunnerResultFileInfo>
)