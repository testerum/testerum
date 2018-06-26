package net.qutester.model.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path

data class RootTreeNode @JsonCreator constructor(
        @JsonProperty("name") override val name: String
    ): ContainerTreeNode {
    override val path: Path = Path.createEmptyInstance();
    override val children: MutableList<TreeNode> = mutableListOf()
}