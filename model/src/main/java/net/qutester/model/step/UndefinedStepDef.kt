package net.qutester.model.step

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.enums.StepPhaseEnum
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.text.StepPattern
import net.qutester.util.StepHashUtil

class UndefinedStepDef @JsonCreator constructor(
        @JsonProperty("phase") override val phase: StepPhaseEnum,
        @JsonProperty("stepPattern") override val stepPattern: StepPattern
): StepDef {
    override val id: String
        get() = StepHashUtil.calculateStepHash(phase, stepPattern)

    override val path: Path
        get() = Path(emptyList(), null, null)

    override val description: String?
        get() = null

    override val tags: List<String>
        get() = emptyList()

    override fun toString() = buildString {
        append(phase)
        append(" ")
        append(stepPattern.toDebugString(varPrefix = "<<", varSuffix = ">>"))
    }

}