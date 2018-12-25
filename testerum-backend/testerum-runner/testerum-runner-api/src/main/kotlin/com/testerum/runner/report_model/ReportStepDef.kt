package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.text.StepPattern

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = ReportUndefinedStepDef::class, name = "UNDEFINED_STEP"),
    JsonSubTypes.Type(value = ReportBasicStepDef::class    , name = "BASIC_STEP"),
    JsonSubTypes.Type(value = ReportComposedStepDef::class , name = "COMPOSED_STEP")
])
interface ReportStepDef {
    val id: String
    val phase: StepPhaseEnum
    val stepPattern: StepPattern
}
