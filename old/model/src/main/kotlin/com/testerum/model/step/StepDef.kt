package com.testerum.model.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.text.StepPattern
import com.testerum.model.warning.Warning


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
        JsonSubTypes.Type(value = UndefinedStepDef::class, name = "UNDEFINED_STEP"),
        JsonSubTypes.Type(value = BasicStepDef::class    , name = "BASIC_STEP"),
        JsonSubTypes.Type(value = ComposedStepDef::class , name = "COMPOSED_STEP")
])
interface StepDef: HasPath {
    val id: String
    override val path: Path
    val phase: StepPhaseEnum
    val stepPattern: StepPattern //TODO: rename to StepSignature
    val description: String?

    val warnings: List<Warning>
    val descendantsHaveWarnings: Boolean

    @get:JsonIgnore
    val hasOwnOrDescendantWarnings: Boolean
        get() = warnings.isNotEmpty() || descendantsHaveWarnings

    @JsonIgnore
    fun getText(): String {
        var result: String = phase.name
        result += " "

        result += stepPattern.getAsText()

        return result
    }
}
