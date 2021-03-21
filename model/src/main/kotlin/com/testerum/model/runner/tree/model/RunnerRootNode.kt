package com.testerum.model.runner.tree.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode

data class RunnerRootNode @JsonCreator constructor(@JsonProperty("name") val name: String): RunnerNode, ContainerTreeNode {
    override val id: String = "rootNode"
    override val path: Path = Path.EMPTY

    @JsonProperty("children") val children: MutableList<RunnerTestOrFeatureNode> = mutableListOf()

    override val childrenCount: Int
        get() = children.size

    override fun addChild(child: TreeNode) {
        val childAsBasicStepNode = child as? RunnerTestOrFeatureNode
            ?: throw IllegalArgumentException("attempted to add child node of unexpected type [${child.javaClass}]: [$child]")
        children += childAsBasicStepNode
    }
}
