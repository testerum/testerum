package net.qutester.model.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.test.TestProperties

data class TestTreeNode @JsonCreator constructor(@JsonProperty("name") override val name: String,
                                                 @JsonProperty("path") override val path: Path,
                                                 @JsonProperty("properties") val properties: TestProperties,
                                                 @JsonProperty("hasOwnOrDescendantWarnings") override val hasOwnOrDescendantWarnings: Boolean = false): TreeNode
