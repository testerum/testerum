package com.testerum.model.step.tree

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.util.new_tree_builder.TreeNode

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = BasicContainerStepNode::class, name = "STEP_BASIC_CONTAINER"),
    JsonSubTypes.Type(value = BasicStepStepNode::class     , name = "STEP_BASIC_STEP")
])
interface BasicStepNode: TreeNode {
    val path: Path
    val hasOwnOrDescendantWarnings: Boolean
}
