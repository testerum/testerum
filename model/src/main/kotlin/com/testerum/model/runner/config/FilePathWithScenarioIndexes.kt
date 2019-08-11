package com.testerum.model.runner.config

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FilePathWithScenarioIndexes @JsonCreator constructor(@JsonProperty("path")            val path: String,
                                                                @JsonProperty("scenarioIndexes") val scenarioIndexes: List<Int>)
