package com.testerum.model.step.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.ComposedStepDef

data class ComposedStepStepNode @JsonCreator constructor(@JsonProperty("path") override val path: Path,
                                                         @JsonProperty("hasOwnOrDescendantWarnings") override val hasOwnOrDescendantWarnings: Boolean = false,
                                                         @JsonProperty("stepDef") val stepDef: ComposedStepDef,
                                                         @JsonProperty("isUsedStep") val isUsedStep: Boolean = true): ComposedStepNode
