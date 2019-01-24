package com.testerum.model.runner.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class RunnerFeatureNode @JsonCreator constructor(@JsonProperty("id") override val id: String,
                                                      @JsonProperty("path") override val path: Path,
                                                      @JsonProperty("name") val name: String,
                                                      @JsonProperty("children") val children: List<RunnerTestOrFeatureNode>): RunnerTestOrFeatureNode
