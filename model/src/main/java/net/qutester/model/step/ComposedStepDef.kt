package net.qutester.model.step

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.enums.StepPhaseEnum
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.text.StepPattern
import net.qutester.util.StepHashUtil

data class ComposedStepDef @JsonCreator constructor(
        @JsonProperty("path") override val path: Path,
        @JsonProperty("phase") override val phase: StepPhaseEnum,
        @JsonProperty("stepPattern") override val stepPattern: StepPattern,
        @JsonProperty("description") override val description: String? = null,

        @JsonProperty("stepCalls") val stepCalls: List<StepCall>

): StepDef {

    override val id: String
        get() = StepHashUtil.calculateStepHash(phase, stepPattern)

    override fun toString() = buildString {
        append(phase)
        append(" ")
        append(stepPattern.toDebugString(varPrefix = "<<", varSuffix = ">>"))
    }
}