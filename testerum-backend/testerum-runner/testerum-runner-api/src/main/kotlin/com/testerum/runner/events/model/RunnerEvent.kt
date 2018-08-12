package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
        JsonSubTypes.Type(value = SuiteStartEvent::class  , name = "TEST_SUITE_START_EVENT"),
        JsonSubTypes.Type(value = SuiteEndEvent::class    , name = "TEST_SUITE_END_EVENT"),
        JsonSubTypes.Type(value = TestStartEvent::class   , name = "TEST_START_EVENT"),
        JsonSubTypes.Type(value = TestEndEvent::class     , name = "TEST_END_EVENT"),
        JsonSubTypes.Type(value = FeatureStartEvent::class, name = "FEATURE_START_EVENT"),
        JsonSubTypes.Type(value = FeatureEndEvent::class  , name = "FEATURE_END_EVENT"),
        JsonSubTypes.Type(value = StepStartEvent::class   , name = "STEP_START_EVENT"),
        JsonSubTypes.Type(value = StepEndEvent::class     , name = "STEP_END_EVENT"),
        JsonSubTypes.Type(value = TextLogEvent::class     , name = "LOG_EVENT"),
        JsonSubTypes.Type(value = RunnerErrorEvent::class , name = "ERROR_EVENT")
])
interface RunnerEvent {
    // todo: convert to UTC; helps for example if the tests are run on different servers that can have different timezones (for we store this in a database); when we produce the report, we should convert from UTC to local timezone
    val time: LocalDateTime

    val eventKey: EventKey

}
