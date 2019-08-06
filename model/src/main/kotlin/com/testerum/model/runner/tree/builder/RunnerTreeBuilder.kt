package com.testerum.model.runner.tree.builder

import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.tree.RunnerBasicStepNode
import com.testerum.model.runner.tree.RunnerComposedStepNode
import com.testerum.model.runner.tree.RunnerFeatureNode
import com.testerum.model.runner.tree.RunnerParametrizedTestNode
import com.testerum.model.runner.tree.RunnerRootNode
import com.testerum.model.runner.tree.RunnerStepNode
import com.testerum.model.runner.tree.RunnerTestNode
import com.testerum.model.runner.tree.RunnerTestOrFeatureNode
import com.testerum.model.runner.tree.RunnerTestScenarioNode
import com.testerum.model.runner.tree.RunnerUndefinedStepNode
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.model.test.scenario.Scenario
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer

class RunnerTreeBuilder {

    val builder = TreeBuilder(RunnerTreeBuilderCustomizer)

    fun addTest(test: TestModel) {
        if (test.properties.isManual) {
            return
        }

        builder.add(test)
    }

    fun build(): RunnerRootNode = builder.build() as RunnerRootNode

    private object RunnerTreeBuilderCustomizer : TreeBuilderCustomizer {
        override fun getPath(payload: Any): List<String> = when (payload) {
            is TestModel -> payload.path.parts
            else         -> throw unknownPayloadException(payload)
        }

        override fun isContainer(payload: Any): Boolean = when (payload) {
            is TestModel -> false
            else         -> throw unknownPayloadException(payload)
        }

        override fun getRootLabel(): String = "Runner"

        override fun getLabel(payload: Any): String = when (payload) {
            is TestModel -> payload.name
            else         -> throw unknownPayloadException(payload)
        }

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<RunnerTestOrFeatureNode> = childrenNodes as List<RunnerTestOrFeatureNode>

            return RunnerRootNode(
                    name = getRootLabel(),
                    children = children
            )
        }

        override fun createNode(payload: Any?, label: String, path: List<String>, childrenNodes: List<Any>, indexInParent: Int): Any {
            @Suppress("UNCHECKED_CAST")
            return when (payload) {
                null -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<RunnerTestOrFeatureNode> = childrenNodes as List<RunnerTestOrFeatureNode>

                    val featurePath = Path(
                            directories = path,
                            fileName = Feature.FILE_NAME_WITHOUT_EXTENSION,
                            fileExtension = Feature.FILE_EXTENSION
                    )
                    RunnerFeatureNode(
                            id = featurePath.toString(),
                            name = label,
                            path = featurePath,
                            children = children
                    )
                }
                is TestModel -> {
                    val isParametrizedTest = payload.scenarios.isNotEmpty()

                    if (isParametrizedTest) {
                        val testScenarios: List<RunnerTestScenarioNode> = payload.scenarios.mapIndexed { index, scenario ->  createTestExecutionBranch(payload, index, scenario) }

                        RunnerParametrizedTestNode(
                                id = payload.id,
                                name = label,
                                path = payload.path,
                                children = testScenarios
                        )
                    } else {
                        val stepCalls: List<RunnerStepNode> = payload.stepCalls.map(this::createStepCallBranch)

                        RunnerTestNode(
                                id = payload.id,
                                name = label,
                                path = payload.path,
                                children = stepCalls
                        )
                    }
                }
                else -> throw unknownPayloadException(payload)
            }
        }

        private fun createTestExecutionBranch(test: TestModel,
                                              index: Int,
                                              scenario: Scenario): RunnerTestScenarioNode {
            val stepCalls: List<RunnerStepNode> = test.stepCalls.map(this::createStepCallBranch)

            return RunnerTestScenarioNode(
                    id = "${test.id}-$index",
                    path = Path.createInstance(
                            "${test.path}/$index"
                    ),
                    name = scenario.name ?: "Execution ${index + 1}",
                    children = stepCalls
            )
        }

        private fun createStepCallBranch(stepCall: StepCall): RunnerStepNode {
            val stepDef = stepCall.stepDef
            val id = stepCall.id
            val path = stepDef.path

            return when (stepDef) {
                is UndefinedStepDef -> RunnerUndefinedStepNode(
                        id = id,
                        path = path,
                        stepCall = stepCall
                )
                is BasicStepDef -> RunnerBasicStepNode(
                        id = id,
                        path = path,
                        stepCall = stepCall
                )
                is ComposedStepDef -> {
                    val childrenNodes: List<RunnerStepNode> = stepDef.stepCalls.map(this::createStepCallBranch)

                    RunnerComposedStepNode(
                            id = id,
                            path = path,
                            stepCall = stepCall,
                            children = childrenNodes
                    )
                }
                else -> throw IllegalArgumentException("unknown step call type [${stepCall.javaClass.name}]")
            }
        }
    }

}
