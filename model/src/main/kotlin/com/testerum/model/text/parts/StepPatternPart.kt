package com.testerum.model.text.parts

import com.fasterxml.jackson.annotation.*
import com.testerum.model.text.parts.param_meta.TypeMeta

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = TextStepPatternPart::class, name = "TEXT"),
    JsonSubTypes.Type(value = ParamStepPatternPart::class, name = "PARAM")
])
sealed class StepPatternPart

data class ParamStepPatternPart @JsonCreator constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("typeMeta") val typeMeta: TypeMeta,
        @JsonProperty("description") val description: String? = null
) : StepPatternPart()

data class TextStepPatternPart @JsonCreator constructor(
        @JsonProperty("text") val text: String
): StepPatternPart()
