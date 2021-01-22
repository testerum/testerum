package com.testerum.model.step

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.text.StepPattern
import com.testerum.model.util.StepHashUtil
import com.testerum.model.warning.Warning

data class ComposedStepDef @JsonCreator constructor(@JsonProperty("path")        override val path: Path,
                                                    @JsonProperty("oldPath")     val oldPath: Path? = path,
                                                    @JsonProperty("phase")       override val phase: StepPhaseEnum,
                                                    @JsonProperty("stepPattern") override val stepPattern: StepPattern,
                                                    @JsonProperty("description") override val description: String? = null,
                                                    @JsonProperty("tags")        val tags: List<String> = emptyList(),
                                                    @JsonProperty("stepCalls")   val stepCalls: List<StepCall>,
                                                    @JsonProperty("warnings")    override val warnings: List<Warning> = emptyList(),
                                                    @JsonProperty("isUsed")      val isUsed: Boolean = true): StepDef {

    companion object {
        val COMPOSED_STEP_FILE_EXTENSION = "step"
    }

    override val id: String
        get() = StepHashUtil.calculateStepHash(phase, stepPattern)

    private val _descendantsHaveWarnings: Boolean = stepCalls.any { it.warnings.isNotEmpty() || it.descendantsHaveWarnings }

    @get:JsonProperty("descendantsHaveWarnings")
    override val descendantsHaveWarnings: Boolean
        get() = _descendantsHaveWarnings

    @JsonIgnore
    fun getNewPath(): Path {
        return path.copy(
                fileName = getText(),
                fileExtension = COMPOSED_STEP_FILE_EXTENSION
        )
    }

    override fun toString() = buildString {
        append(phase)
        append(" ")
        append(stepPattern.toDebugString(varPrefix = "<<", varSuffix = ">>"))
        append(" (").append(stepCalls.size).append(" calls)")
    }
}
