package com.testerum.model.runner.old_tree

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.testerum.model.infrastructure.path.Path

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = OldRunnerRootNode::class            , name = "RUNNER_ROOT"),
    JsonSubTypes.Type(value = OldRunnerFeatureNode::class         , name = "RUNNER_FEATURE"),
    JsonSubTypes.Type(value = OldRunnerTestNode::class            , name = "RUNNER_TEST"),
    JsonSubTypes.Type(value = OldRunnerParametrizedTestNode::class, name = "RUNNER_PARAMETRIZED_TEST"),
    JsonSubTypes.Type(value = OldRunnerScenarioNode::class    , name = "RUNNER_TEST_SCENARIO"),
    JsonSubTypes.Type(value = OldRunnerUndefinedStepNode::class   , name = "RUNNER_UNDEFINED_STEP"),
    JsonSubTypes.Type(value = OldRunnerBasicStepNode::class       , name = "RUNNER_BASIC_STEP"),
    JsonSubTypes.Type(value = OldRunnerComposedStepNode::class    , name = "RUNNER_COMPOSED_STEP")
])
interface OldRunnerNode {
    val id: String
    val path: Path
}
