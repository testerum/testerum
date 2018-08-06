package com.testerum.model.step.operation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.ComposedStepDef

data class UpdateComposedStepDef @JsonCreator constructor(@JsonProperty("oldPath") val oldPath: Path,
                                                          @JsonProperty("composedStepDef") val composedStepDef: ComposedStepDef)
