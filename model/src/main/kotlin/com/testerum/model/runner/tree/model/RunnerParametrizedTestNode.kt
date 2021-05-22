package com.testerum.model.runner.tree.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode

data class RunnerParametrizedTestNode @JsonCreator constructor(
    @JsonProperty("id") override val id: String,
    @JsonProperty("path") override val path: Path,
    @JsonProperty("name") val name: String
): RunnerTestOrFeatureNode, ContainerTreeNode {

    @JsonProperty("children") val children: MutableList<RunnerScenarioNode> = mutableListOf()

    override val childrenCount: Int
        get() = children.size

    override fun addChild(child: TreeNode) {
        val childAsRunnerScenarioNode = child as? RunnerScenarioNode
            ?: throw IllegalArgumentException("attempted to add child node of unexpected type [${child.javaClass}]: [$child]")
        children += childAsRunnerScenarioNode
    }
}
