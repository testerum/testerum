package net.qutester.model.step

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.enums.StepPhaseEnum
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.text.StepPattern
import net.qutester.model.text.parts.ParamStepPatternPart
import net.qutester.model.warning.Warning

data class BasicStepDef @JsonCreator constructor(@JsonProperty("phase") override val phase: StepPhaseEnum,
                                                 @JsonProperty("stepPattern") override val stepPattern: StepPattern,
                                                 @JsonProperty("description") override val description: String? = null,
                                                 @JsonProperty("tags") override val tags: List<String> = emptyList(),
                                                 @JsonProperty("className") val className: String,
                                                 @JsonProperty("methodName") val methodName: String,
                                                 @JsonProperty("warnings") override val warnings: List<Warning> = emptyList()): StepDef {

    private val _path = run {
        val paramTypes = stepPattern.patternParts
                .filter { it is ParamStepPatternPart }
                .map { (it as ParamStepPatternPart).type }

        val packagesWithClass: List<String> = className.split('.')
        val packages: List<String> = packagesWithClass.dropLast(1)
        val classNameWithoutPackages = packagesWithClass.last();

        return@run Path(packages, classNameWithoutPackages, methodName +"(" + paramTypes.joinToString(",")+")")
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