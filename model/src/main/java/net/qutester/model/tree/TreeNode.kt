package net.qutester.model.tree

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.qutester.model.infrastructure.path.Path

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = RootTreeNode::class   , name = "ROOT_NODE"),
    JsonSubTypes.Type(value = FeatureTreeNode::class, name = "FEATURE_NODE"),
    JsonSubTypes.Type(value = TestTreeNode::class   , name = "TEST_NODE")
])
interface TreeNode {
    val name: String
    val path: Path
    val hasOwnOrDescendantWarnings: Boolean
}