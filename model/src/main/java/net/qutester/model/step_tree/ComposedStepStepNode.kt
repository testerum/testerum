package net.qutester.model.step_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.enums.StepPhaseEnum
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.text.StepPattern

data class ComposedStepStepNode @JsonCreator constructor(@JsonProperty("path") override val path: Path,
                                                         @JsonProperty("hasOwnOrDescendantWarnings") override val hasOwnOrDescendantWarnings: Boolean = false,
                                                         @JsonProperty("phase") val phase: StepPhaseEnum,
                                                         @JsonProperty("stepPattern") val stepPattern: StepPattern): ComposedStepNode
