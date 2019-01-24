package com.testerum.model.step

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.warning.Warning

data class BasicStepDef @JsonCreator constructor(@JsonProperty("phase")       override val phase: StepPhaseEnum,
                                                 @JsonProperty("stepPattern") override val stepPattern: StepPattern,
                                                 @JsonProperty("description") override val description: String? = null,
                                                 @JsonProperty("tags")        val tags: List<String> = emptyList(),
                                                 @JsonProperty("className")   val className: String,
                                                 @JsonProperty("methodName")  val methodName: String,
                                                 @JsonProperty("warnings")    override val warnings: List<Warning> = emptyList()): StepDef {

    private val _path = run {
        val paramTypes = stepPattern.patternParts
                .filter { it is ParamStepPatternPart }
                .map { (it as ParamStepPatternPart).type }

        val packagesWithClass: List<String> = className.split('.')
        val packages: List<String> = packagesWithClass.dropLast(1)
        val classNameWithoutPackages = packagesWithClass.last()

        return@run Path(packages, classNameWithoutPackages, methodName + "(" + paramTypes.joinToString(",") + ")")
    }

    override val path: Path
        get() = _path

    private val _id: String = _path.toString()

    override val id: String
        get() = _id

    @get:JsonProperty("descendantsHaveWarnings")
    override val descendantsHaveWarnings: Boolean
        get() = false // basic steps don't have descendants

    override fun toString() = buildString {
        append(phase)
        append(" ")
        append(stepPattern.toDebugString(varPrefix = "<<", varSuffix = ">>"))
    }

}
