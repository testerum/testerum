package com.testerum.model.runner.tree

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.testerum.model.infrastructure.path.Path

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = RunnerRootNode::class            , name = "RUNNER_ROOT"),
    JsonSubTypes.Type(value = RunnerFeatureNode::class         , name = "RUNNER_FEATURE"),
    JsonSubTypes.Type(value = RunnerTestNode::class            , name = "RUNNER_TEST"),
    JsonSubTypes.Type(value = RunnerParametrizedTestNode::class, name = "RUNNER_PARAMETRIZED_TEST"),
    JsonSubTypes.Type(value = RunnerScenarioNode::class    , name = "RUNNER_TEST_SCENARIO"),
    JsonSubTypes.Type(value = RunnerUndefinedStepNode::class   , name = "RUNNER_UNDEFINED_STEP"),
    JsonSubTypes.Type(value = RunnerBasicStepNode::class       , name = "RUNNER_BASIC_STEP"),
    JsonSubTypes.Type(value = RunnerComposedStepNode::class    , name = "RUNNER_COMPOSED_STEP")
])
interface RunnerNode {
    val id: String
    val path: Path
}
