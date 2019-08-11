package com.testerum.model.runner.config

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class PathWithScenarioIndexes @JsonCreator constructor(@JsonProperty("path")            val path: Path,
                                                            @JsonProperty("scenarioIndexes") val scenarioIndexes: List<Int>)
