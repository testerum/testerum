package com.testerum.model.runner.tree.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode

data class RunnerFeatureNode @JsonCreator constructor(
    @JsonProperty("id") override val id: String,
    @JsonProperty("path") override val path: Path,
    @JsonProperty("name") val name: String,
    @JsonProperty("beforeAllHooks") val beforeAllHooks: List<RunnerStepNode>,
    @JsonProperty("beforeEachHooks") val beforeEachHooks: List<RunnerStepNode>,
    @JsonProperty("afterEachHooks") val afterEachHooks: List<RunnerStepNode>,
    @JsonProperty("afterAllHooks") val afterAllHooks: List<RunnerStepNode>,
) : RunnerTestOrFeatureNode, ContainerTreeNode {

    @JsonProperty("children") val children: MutableList<RunnerTestOrFeatureNode> = mutableListOf()

    override val childrenCount: Int
        get() = children.size

    override fun addChild(child: TreeNode) {
        val childAsRunnerTestOrFeatureNode = child as? RunnerTestOrFeatureNode
            ?: throw IllegalArgumentException("attempted to add child node of unexpected type [${child.javaClass}]: [$child]")
        children += childAsRunnerTestOrFeatureNode
    }
}
