package net.qutester.model.step_tree

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.qutester.model.infrastructure.path.Path

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = ComposedContainerStepNode::class, name = "STEP_COMPOSED_CONTAINER"),
    JsonSubTypes.Type(value = ComposedStepStepNode::class     , name = "STEP_COMPOSED_STEP")
])
interface ComposedStepNode {
    val path: Path
    val hasOwnOrDescendantWarnings: Boolean
}
