package com.testerum.model.step.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode

data class ComposedContainerStepNode @JsonCreator constructor(@JsonProperty("path") override val path: Path,
                                                              @JsonProperty("name") val name: String) : ComposedStepNode, ContainerTreeNode {

    @JsonProperty("children") val children: MutableList<ComposedStepNode> = mutableListOf()
    @JsonProperty("hasOwnOrDescendantWarnings") override var hasOwnOrDescendantWarnings: Boolean = false

    override val childrenCount: Int
        get() = children.size

    override fun addChild(child: TreeNode) {
        val childAsBasicStepNode = child as? ComposedStepNode
            ?: throw IllegalArgumentException("attempted to add child node of unexpected type [${child.javaClass}]: [$child]")
        children += childAsBasicStepNode

        if (childAsBasicStepNode.hasOwnOrDescendantWarnings) {
            this.hasOwnOrDescendantWarnings = true
        }
    }
}
