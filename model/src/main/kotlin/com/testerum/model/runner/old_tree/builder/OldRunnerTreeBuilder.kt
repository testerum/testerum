package com.testerum.model.runner.old_tree.builder

import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.old_tree.OldRunnerBasicStepNode
import com.testerum.model.runner.old_tree.OldRunnerComposedStepNode
import com.testerum.model.runner.old_tree.OldRunnerFeatureNode
import com.testerum.model.runner.old_tree.OldRunnerParametrizedTestNode
import com.testerum.model.runner.old_tree.OldRunnerRootNode
import com.testerum.model.runner.old_tree.OldRunnerScenarioNode
import com.testerum.model.runner.old_tree.OldRunnerStepNode
import com.testerum.model.runner.old_tree.OldRunnerTestNode
import com.testerum.model.runner.old_tree.OldRunnerTestOrFeatureNode
import com.testerum.model.runner.old_tree.OldRunnerUndefinedStepNode
import com.testerum.model.runner.tree.TestPathAndModel
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.model.test.scenario.Scenario
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer

class OldRunnerTreeBuilder {

    val builder = TreeBuilder(RunnerTreeBuilderCustomizer)

    fun addTest(test: TestPathAndModel) {
        if (test.model.properties.isManual) {
            return
        }

        builder.add(test)
    }

    fun build(): OldRunnerRootNode = builder.build() as OldRunnerRootNode

    private object RunnerTreeBuilderCustomizer : TreeBuilderCustomizer {
        override fun getPath(payload: Any): List<String> = when (payload) {
            is TestPathAndModel -> payload.model.path.parts
            else                -> throw unknownPayloadException(payload)
        }

        override fun isContainer(payload: Any): Boolean = when (payload) {
            is TestPathAndModel -> false
            else                -> throw unknownPayloadException(payload)
        }

        override fun getRootLabel(): String = "Runner"

        override fun getLabel(payload: Any): String = when (payload) {
            is TestPathAndModel -> payload.model.name
            else                -> throw unknownPayloadException(payload)
        }

        override fun createRootNode(payload: Any?, childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<OldRunnerTestOrFeatureNode> = childrenNodes as List<OldRunnerTestOrFeatureNode>

            return OldRunnerRootNode(
                    name = getRootLabel(),
                    children = children
            )
        }

        override fun createNode(payload: Any?, label: String, path: List<String>, childrenNodes: List<Any>, indexInParent: Int): Any {
            @Suppress("UNCHECKED_CAST")
            return when (payload) {
                null -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<OldRunnerTestOrFeatureNode> = childrenNodes as List<OldRunnerTestOrFeatureNode>

                    val featurePath = Path(
                            directories = path,
                            fileName = Feature.FILE_NAME_WITHOUT_EXTENSION,
                            fileExtension = Feature.FILE_EXTENSION
                    )
                    OldRunnerFeatureNode(
                            id = featurePath.toString(),
                            name = label,
                            path = featurePath,
                            children = children
                    )
                }
                is TestPathAndModel -> {
                    val isParametrizedTest = payload.model.scenarios.isNotEmpty()

                    if (isParametrizedTest) {
                        val scenariosWithOriginalIndex = payload.model.scenarios.mapIndexed { index, scenario ->
                            index to scenario
                        }

                        val filteredTestScenarios = if (payload.testPath is ScenariosTestPath) {
                            if (payload.testPath.scenarioIndexes.isEmpty()) {
                                // there is no filter on scenarios
                                scenariosWithOriginalIndex
                            } else {
                                scenariosWithOriginalIndex.filterIndexed { scenarioIndex, _ ->
                                    scenarioIndex in payload.testPath.scenarioIndexes
                                }
                            }
                        } else {
                            scenariosWithOriginalIndex
                        }


                        val runnerScenariosNodes: List<OldRunnerScenarioNode> = filteredTestScenarios.mapIndexed { filteredScenarioIndex, scenarioWithOriginalIndex ->
                            createTestScenarioBranch(payload.model, scenarioWithOriginalIndex, filteredScenarioIndex)
                        }

                        OldRunnerParametrizedTestNode(
                                id = payload.model.id,
                                name = label,
                                path = payload.model.path,
                                children = runnerScenariosNodes
                        )
                    } else {
                        val stepCalls: List<OldRunnerStepNode> = payload.model.stepCalls.map(this::createStepCallBranch)

                        OldRunnerTestNode(
                                id = payload.model.id,
                                name = label,
                                path = payload.model.path,
                                enabled = !payload.model.properties.isDisabled,
                                children = stepCalls
                        )
                    }
                }
                else -> throw unknownPayloadException(payload)
            }
        }

        private fun createTestScenarioBranch(test: TestModel,
                                             scenarioWithOriginalIndex: Pair<Int, Scenario>,
                                             filteredScenarioIndex: Int): OldRunnerScenarioNode {
            val originalScenarioIndex = scenarioWithOriginalIndex.first
            val scenario = scenarioWithOriginalIndex.second

            val stepCalls: List<OldRunnerStepNode> = test.stepCalls.map(this::createStepCallBranch)

            return OldRunnerScenarioNode(
                    id = "${test.id}-$filteredScenarioIndex",
                    path = test.path,
                    scenarioIndex = filteredScenarioIndex,
                    name = scenario.name ?: "Scenario ${originalScenarioIndex + 1}",
                    enabled = scenario.enabled,
                    children = stepCalls
            )
        }

        private fun createStepCallBranch(stepCall: StepCall): OldRunnerStepNode {
            val stepDef = stepCall.stepDef
            val id = stepCall.id
            val path = stepDef.path

            return when (stepDef) {
                is UndefinedStepDef -> OldRunnerUndefinedStepNode(
                        id = id,
                        path = path,
                        stepCall = stepCall
                )
                is BasicStepDef -> OldRunnerBasicStepNode(
                        id = id,
                        path = path,
                        stepCall = stepCall
                )
                is ComposedStepDef -> {
                    val childrenNodes: List<OldRunnerStepNode> = stepDef.stepCalls.map(this::createStepCallBranch)

                    OldRunnerComposedStepNode(
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
