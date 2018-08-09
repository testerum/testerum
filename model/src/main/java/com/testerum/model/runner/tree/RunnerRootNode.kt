package com.testerum.model.runner.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class RunnerRootNode @JsonCreator constructor(@JsonProperty("name") override val name: String,
                                                   @JsonProperty("children") override val children: List<RunnerNode>): RunnerContainerNode {
    override val id: String = "rootNode"
    override val path: Path = Path.EMPTY
}