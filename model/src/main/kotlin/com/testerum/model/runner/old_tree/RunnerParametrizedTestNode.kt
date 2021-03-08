package com.testerum.model.runner.old_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class RunnerParametrizedTestNode @JsonCreator constructor(@JsonProperty("id") override val id: String,
                                                               @JsonProperty("path") override val path: Path,
                                                               @JsonProperty("name") val name: String,
                                                               @JsonProperty("children") val children: List<RunnerScenarioNode>): RunnerTestOrFeatureNode
