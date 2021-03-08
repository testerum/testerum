package com.testerum.model.runner.old_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class RunnerRootNode @JsonCreator constructor(@JsonProperty("name") val name: String,
                                                   @JsonProperty("children") val children: List<RunnerTestOrFeatureNode>): RunnerNode {
    override val id: String = "rootNode"
    override val path: Path = Path.EMPTY

}
