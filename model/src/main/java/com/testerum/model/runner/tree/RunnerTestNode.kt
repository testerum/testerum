package com.testerum.model.runner.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.tree.RunnerNode
import com.testerum.model.test.TestProperties

data class RunnerTestNode @JsonCreator constructor(@JsonProperty("id") override val id: String,
                                                   @JsonProperty("name") override val name: String,
                                                   @JsonProperty("path") override val path: Path,
                                                   @JsonProperty("children") override val children: List<RunnerNode>): RunnerContainerNode
