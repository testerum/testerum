package net.qutester.model.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path

data class FeatureTreeNode  @JsonCreator constructor(@JsonProperty("name") override val name: String,
                                                     @JsonProperty("path") override val path: Path,
                                                     @JsonProperty("children") override val children: List<TreeNode>,
                                                     @JsonProperty("hasOwnOrDescendantWarnings") override val hasOwnOrDescendantWarnings: Boolean = false): ContainerTreeNode