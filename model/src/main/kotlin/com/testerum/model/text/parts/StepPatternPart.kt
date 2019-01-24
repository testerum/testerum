package com.testerum.model.text.parts

import com.fasterxml.jackson.annotation.*

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = TextStepPatternPart::class, name = "TEXT"),
    JsonSubTypes.Type(value = ParamStepPatternPart::class, name = "PARAM")
])
sealed class StepPatternPart

data class ParamStepPatternPart @JsonCreator constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("type") val type: String,
        @JsonProperty("description") val description: String? = null,
        @JsonProperty("enumValues") val enumValues: List<String> = emptyList()
) : StepPatternPart()

data class TextStepPatternPart @JsonCreator constructor(
        @JsonProperty("text") val text: String
): StepPatternPart()
