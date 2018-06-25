package net.qutester.model.step.operation

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.step.ComposedStepDef
import net.qutester.model.infrastructure.path.Path

data class UpdateComposedStepDef @JsonCreator constructor(
        @JsonProperty("oldPath") val oldPath: Path,
        @JsonProperty("composedStepDef") val composedStepDef: ComposedStepDef){
}