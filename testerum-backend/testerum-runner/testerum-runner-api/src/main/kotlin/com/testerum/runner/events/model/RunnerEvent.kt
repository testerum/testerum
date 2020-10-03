package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
        JsonSubTypes.Type(value = ConfigurationEvent::class         , name = "CONFIGURATION_EVENT"),
        JsonSubTypes.Type(value = SuiteStartEvent::class            , name = "TEST_SUITE_START_EVENT"),
        JsonSubTypes.Type(value = SuiteEndEvent::class              , name = "TEST_SUITE_END_EVENT"),
        JsonSubTypes.Type(value = FeatureStartEvent::class          , name = "FEATURE_START_EVENT"),
        JsonSubTypes.Type(value = FeatureEndEvent::class            , name = "FEATURE_END_EVENT"),
        JsonSubTypes.Type(value = TestStartEvent::class             , name = "TEST_START_EVENT"),
        JsonSubTypes.Type(value = TestEndEvent::class               , name = "TEST_END_EVENT"),
        JsonSubTypes.Type(value = ParametrizedTestStartEvent::class , name = "PARAMETRIZED_TEST_START_EVENT"),
        JsonSubTypes.Type(value = ParametrizedTestEndEvent::class   , name = "PARAMETRIZED_TEST_END_EVENT"),
        JsonSubTypes.Type(value = ScenarioStartEvent::class         , name = "SCENARIO_START_EVENT"),
        JsonSubTypes.Type(value = ScenarioEndEvent::class           , name = "SCENARIO_END_EVENT"),
        JsonSubTypes.Type(value = StepStartEvent::class             , name = "STEP_START_EVENT"),
        JsonSubTypes.Type(value = StepEndEvent::class               , name = "STEP_END_EVENT"),
        JsonSubTypes.Type(value = TextLogEvent::class               , name = "LOG_EVENT"),
        JsonSubTypes.Type(value = RunnerErrorEvent::class           , name = "ERROR_EVENT"),
        JsonSubTypes.Type(value = RunnerStoppedEvent::class         , name = "RUNNER_STOPPED_EVENT")
])
interface RunnerEvent {

    val time: LocalDateTime

    val eventKey: EventKey

}
