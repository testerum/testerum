package com.testerum.model.runner.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class RunnerScenarioNode @JsonCreator constructor(@JsonProperty("id") override val id: String,
                                                       @JsonProperty("path") override val path: Path,
                                                       @JsonProperty("scenarioIndex") val scenarioIndex: Int,
                                                       @JsonProperty("name") val name: String,
                                                       @JsonProperty("enabled") val enabled: Boolean,
                                                       @JsonProperty("children") val children: List<RunnerStepNode>): RunnerTestOrFeatureNode
