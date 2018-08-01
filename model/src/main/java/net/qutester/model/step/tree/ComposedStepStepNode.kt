package net.qutester.model.step.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.step.ComposedStepDef

data class ComposedStepStepNode @JsonCreator constructor(@JsonProperty("path") override val path: Path,
                                                         @JsonProperty("hasOwnOrDescendantWarnings") override val hasOwnOrDescendantWarnings: Boolean = false,
                                                         @JsonProperty("stepDef") val stepDef: ComposedStepDef): ComposedStepNode
