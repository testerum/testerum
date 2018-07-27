package net.qutester.model.step_tree

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.qutester.model.infrastructure.path.Path

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = BasicContainerStepNode::class, name = "STEP_BASIC_CONTAINER"),
    JsonSubTypes.Type(value = BasicStepStepNode::class     , name = "STEP_BASIC_STEP")
])
interface BasicStepNode {
    val path: Path
    val hasOwnOrDescendantWarnings: Boolean
}
