package com.testerum.runner.report_model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = ReportSuite::class   , name = "SUITE"),
    JsonSubTypes.Type(value = ReportFeature::class , name = "FEATURE"),
    JsonSubTypes.Type(value = ReportTest::class    , name = "TEST"),
    JsonSubTypes.Type(value = ReportStep::class    , name = "STEP")
])
interface RunnerReportNode {

    val logs: List<ReportLog>

}
