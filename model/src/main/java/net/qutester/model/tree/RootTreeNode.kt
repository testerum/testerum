package net.qutester.model.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path

data class RootTreeNode @JsonCreator constructor(@JsonProperty("name") override val name: String,
                                                 @JsonProperty("children") override val children: List<TreeNode>,
                                                 @JsonProperty("hasOwnOrDescendantWarnings") override val hasOwnOrDescendantWarnings: Boolean = false): ContainerTreeNode {
    override val path: Path = Path.EMPTY
}