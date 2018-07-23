package net.qutester.model.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.qutester.model.enums.StepPhaseEnum
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.text.StepPattern


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
        JsonSubTypes.Type(value = BasicStepDef::class, name = "BASIC_STEP"),
        JsonSubTypes.Type(value = ComposedStepDef::class, name = "COMPOSED_STEP"),
        JsonSubTypes.Type(value = UndefinedStepDef::class, name = "UNDEFINED_STEP")
])
interface StepDef {
    val id: String
    val path: Path
    val phase: StepPhaseEnum
    val stepPattern: StepPattern //TODO: rename to StepSignature
    val description: String?
    val tags: List<String>

    @JsonIgnore
    fun getText(): String {
        var result: String = phase.name;
        result += " ";

        result += stepPattern.getAsText();

        return result;
    }
}