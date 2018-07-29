package net.qutester.model.step.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RootStepNode @JsonCreator constructor(@JsonProperty("name") val name: String,
                                                 @JsonProperty("basicStepsRoot") val basicStepsRoot: BasicContainerStepNode,
                                                 @JsonProperty("composedStepsRoot") val composedStepsRoot: ComposedContainerStepNode,
                                                 @JsonProperty("hasOwnOrDescendantWarnings") val hasOwnOrDescendantWarnings: Boolean = false)
