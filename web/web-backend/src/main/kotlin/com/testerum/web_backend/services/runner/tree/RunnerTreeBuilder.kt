package com.testerum.model.runner.tree

import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.tree.model.RunnerBasicStepNode
import com.testerum.model.runner.tree.model.RunnerComposedStepNode
import com.testerum.model.runner.tree.model.RunnerFeatureNode
import com.testerum.model.runner.tree.model.RunnerParametrizedTestNode
import com.testerum.model.runner.tree.model.RunnerRootNode
import com.testerum.model.runner.tree.model.RunnerScenarioNode
import com.testerum.model.runner.tree.model.RunnerStepNode
import com.testerum.model.runner.tree.model.RunnerTestNode
import com.testerum.model.runner.tree.model.RunnerUndefinedStepNode
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.model.test.scenario.Scenario
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.PathBasedTreeBuilder
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.model.util.new_tree_builder.TreeNodeFactory

class RunnerTreeBuilder(val featureCache: FeaturesCache) {

    fun build(tests: List<TestPathAndModel>): RunnerRootNode {
        val builder = PathBasedTreeBuilder(
            nodeFactory = RunnerTreeFactory(featureCache)
        )
        return builder.createTree(tests)
    }
}

private class RunnerTreeFactory(val featureCache: FeaturesCache) : TreeNodeFactory<RunnerRootNode, RunnerFeatureNode> {
    override fun createRootNode(item: HasPath?): RunnerRootNode {
        return RunnerRootNode("Runner")
    }

    override fun createVirtualContainer(parentNode: ContainerTreeNode, path: Path): RunnerFeatureNode {
        val feature = featureCache.getFeatureAtPath(path) ?: throw RuntimeException("Feature at path [${path.toString()}] is not found in cache")
        val hooks = feature.hooks

        val beforeAllHooks = hooks.beforeAll.map { createStepCallBranch(it) }
        val beforeEachHooks = hooks.beforeEach.map { createStepCallBranch(it) }
        val afterEachHooks = hooks.afterEach.map { createStepCallBranch(it) }
        val afterAllHooks = hooks.afterAll.map { createStepCallBranch(it) }

        return RunnerFeatureNode(
            id = path.toString(),
            name = path.directories.last(),
            path = path,
            beforeAllHooks = beforeAllHooks,
            beforeEachHooks = beforeEachHooks,
            afterEachHooks = afterEachHooks,
            afterAllHooks = afterAllHooks
        )
    }

    override fun createNode(parentNode: ContainerTreeNode, item: HasPath): TreeNode {
        val testPathAndModel = item as TestPathAndModel
            ?: throw IllegalArgumentException("unexpected type")

        val isParametrizedTest = testPathAndModel.model.scenarios.isNotEmpty()

        if (isParametrizedTest) {
            val scenariosWithOriginalIndex = testPathAndModel.model.scenarios.mapIndexed { index, scenario ->
                index to scenario
            }

            val filteredTestScenarios = if (testPathAndModel.testPath is ScenariosTestPath) {
                if ((testPathAndModel.testPath as ScenariosTestPath).scenarioIndexes.isEmpty()) {
                    // there is no filter on scenarios
                    scenariosWithOriginalIndex
                } else {
                    scenariosWithOriginalIndex.filterIndexed { scenarioIndex, _ ->
                        scenarioIndex in (testPathAndModel.testPath as ScenariosTestPath).scenarioIndexes
                    }
                }
            } else {
                scenariosWithOriginalIndex
            }

            val runnerScenariosNodes: List<RunnerScenarioNode> = filteredTestScenarios.mapIndexed { filteredScenarioIndex, scenarioWithOriginalIndex ->
                createTestScenarioBranch(
                    testPathAndModel.model,
                    scenarioWithOriginalIndex,
                    filteredScenarioIndex
                )
            }

            val runnerParametrizedTestNode = RunnerParametrizedTestNode(
                id = testPathAndModel.model.id,
                name = testPathAndModel.model.name,
                path = testPathAndModel.model.path
            )
            runnerScenariosNodes.forEach{runnerParametrizedTestNode.addChild(it)}

            return runnerParametrizedTestNode

        } else {
            val stepCalls: List<RunnerStepNode> = testPathAndModel.model.stepCalls.map(this::createStepCallBranch)

            val runnerTestNode = RunnerTestNode(
                id = testPathAndModel.model.id,
                name = testPathAndModel.model.name,
                path = testPathAndModel.model.path,
                enabled = !testPathAndModel.model.properties.isDisabled
            )
            stepCalls.forEach {runnerTestNode.addChild(it)}

            return runnerTestNode
        }
    }

    private fun createTestScenarioBranch(test: TestModel,
                                         scenarioWithOriginalIndex: Pair<Int, Scenario>,
                                         filteredScenarioIndex: Int): RunnerScenarioNode {
        val originalScenarioIndex = scenarioWithOriginalIndex.first
        val scenario = scenarioWithOriginalIndex.second

        val stepCalls: List<RunnerStepNode> = test.stepCalls.map(this::createStepCallBranch)

        val runnerScenarioNode = RunnerScenarioNode(
            id = "${test.id}-$filteredScenarioIndex",
            path = test.path,
            scenarioIndex = filteredScenarioIndex,
            name = scenario.name ?: "Scenario ${originalScenarioIndex + 1}",
            enabled = scenario.enabled
        )
        stepCalls.forEach { runnerScenarioNode.addChild(it) }

        return runnerScenarioNode
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

                val runnerComposedStepNode = RunnerComposedStepNode(
                    id = id,
                    path = path,
                    stepCall = stepCall
                )
                childrenNodes.forEach { runnerComposedStepNode.addChild(it) }

                runnerComposedStepNode
            }
            else -> throw IllegalArgumentException("unknown step call type [${stepCall.javaClass.name}]")
        }
    }
}
