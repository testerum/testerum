package com.testerum.model.runner.tree.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.tree.model.enums.HooksType
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode

data class RunnerHooksNode (
    @JsonProperty("id") override val id: String,
    @JsonIgnore val hookType: HooksType,
    @JsonIgnore val parentPath: Path,
    @JsonIgnore val hooks: List<RunnerStepNode>
) : ContainerTreeNode, RunnerNode {

    @JsonProperty("path") override val path: Path = parentPath
    @JsonProperty("name") val name: String = hookType.uiName
    @JsonProperty("children") val children: List<RunnerStepNode> = hooks

    @get:JsonIgnore()
    override val childrenCount: Int
        get() = children.size

    override fun addChild(child: TreeNode) {}
}
