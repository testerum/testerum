package com.testerum.model.runner.tree

import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.model.feature.Feature
import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.tree.id.RunnerIdCreator
import com.testerum.model.runner.tree.model.RunnerBasicStepNode
import com.testerum.model.runner.tree.model.RunnerComposedStepNode
import com.testerum.model.runner.tree.model.RunnerFeatureNode
import com.testerum.model.runner.tree.model.RunnerHooksContainerNode
import com.testerum.model.runner.tree.model.RunnerHooksNode
import com.testerum.model.runner.tree.model.RunnerNode
import com.testerum.model.runner.tree.model.RunnerParametrizedTestNode
import com.testerum.model.runner.tree.model.RunnerRootNode
import com.testerum.model.runner.tree.model.RunnerScenarioNode
import com.testerum.model.runner.tree.model.RunnerStepNode
import com.testerum.model.runner.tree.model.RunnerTestNode
import com.testerum.model.runner.tree.model.RunnerUndefinedStepNode
import com.testerum.model.runner.tree.model.enums.HooksType.AFTER_ALL_HOOKS
import com.testerum.model.runner.tree.model.enums.HooksType.AFTER_EACH_HOOKS
import com.testerum.model.runner.tree.model.enums.HooksType.AFTER_HOOKS
import com.testerum.model.runner.tree.model.enums.HooksType.BEFORE_ALL_HOOKS
import com.testerum.model.runner.tree.model.enums.HooksType.BEFORE_EACH_HOOKS
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
        val root = featureCache.getFeatureAtPath(Path.EMPTY) ?: Feature.createVirtualFeature(Path.EMPTY)

        val rootNodeId = RunnerIdCreator.getRootId()
        val beforeAllId = RunnerIdCreator.getHookContainerId(rootNodeId, HookPhase.BEFORE_ALL_TESTS)
        val afterAllId = RunnerIdCreator.getHookContainerId(rootNodeId, HookPhase.AFTER_ALL_TESTS)

        val beforeAllHooks = root.hooks.beforeAll.mapIndexed {idx, it -> createStepCallBranch(it, idx, beforeAllId)}
        val afterAllHooks = root.hooks.afterAll.mapIndexed {idx, it -> createStepCallBranch(it, idx, afterAllId)}

        return RunnerRootNode(
            id = rootNodeId,
            name = "Runner",
            beforeAllHooks = RunnerHooksNode(beforeAllId, BEFORE_ALL_HOOKS, Path.EMPTY, beforeAllHooks),
            beforeEachStepCalls = root.hooks.beforeEach,
            afterEachStepCalls = root.hooks.afterEach,
            afterAllHooks = RunnerHooksNode(afterAllId, AFTER_ALL_HOOKS, Path.EMPTY, afterAllHooks)
        )
    }

    override fun createVirtualContainer(parentNode: ContainerTreeNode, path: Path): RunnerFeatureNode {
        val feature = featureCache.getFeatureAtPath(path) ?: Feature.createVirtualFeature(Path.EMPTY)

        val parentNodeAsHooksContainer = parentNode as RunnerHooksContainerNode
        val featureId = RunnerIdCreator.getFeatureId(parentNodeAsHooksContainer.id, path)
        val beforeAllId = RunnerIdCreator.getHookContainerId(featureId, HookPhase.BEFORE_ALL_TESTS)
        val afterAllId = RunnerIdCreator.getHookContainerId(featureId, HookPhase.AFTER_ALL_TESTS)

        val beforeAllHooks = feature.hooks.beforeAll.mapIndexed {idx, it -> createStepCallBranch(it, idx, beforeAllId)}
        val afterAllHooks = feature.hooks.afterAll.mapIndexed {idx, it -> createStepCallBranch(it, idx, afterAllId)}

        return RunnerFeatureNode(
            id = featureId,
            name = path.directories.last(),
            path = path,
            beforeAllHooks = RunnerHooksNode(beforeAllId, BEFORE_ALL_HOOKS, Path.EMPTY, beforeAllHooks),
            beforeEachStepCalls = parentNodeAsHooksContainer.beforeEachStepCalls + feature.hooks.beforeEach,
            afterEachStepCalls = feature.hooks.afterEach + parentNodeAsHooksContainer.afterEachStepCalls,
            afterAllHooks = RunnerHooksNode(afterAllId, AFTER_ALL_HOOKS, Path.EMPTY, afterAllHooks)
        )
    }

    override fun createNode(parentNode: ContainerTreeNode, item: HasPath): TreeNode {
        val testPathAndModel = item as TestPathAndModel

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

            val runnerParametrizedTestNode = RunnerParametrizedTestNode(
                id = RunnerIdCreator.getParametrizedTestId((parentNode as RunnerNode).id, testPathAndModel.model),
                name = testPathAndModel.model.name,
                path = testPathAndModel.model.path
            )

            val runnerScenariosNodes: List<RunnerScenarioNode> = filteredTestScenarios.mapIndexed { filteredScenarioIndex, scenarioWithOriginalIndex ->
                createTestScenarioBranch(
                    parentNode = runnerParametrizedTestNode,
                    parentHooksContainer = (parentNode as RunnerHooksContainerNode),
                    test = testPathAndModel.model,
                    scenarioWithOriginalIndex = scenarioWithOriginalIndex,
                    filteredScenarioIndex = filteredScenarioIndex
                )
            }

            runnerScenariosNodes.forEach{runnerParametrizedTestNode.addChild(it)}

            return runnerParametrizedTestNode

        } else {

            val testId = RunnerIdCreator.getTestId((parentNode as RunnerNode).id, testPathAndModel.model)
            val parentNodeAsHooksContainer = parentNode as RunnerHooksContainerNode
            val beforeEachId = RunnerIdCreator.getHookContainerId(testId, HookPhase.BEFORE_EACH_TEST)
            val afterEachId = RunnerIdCreator.getHookContainerId(testId, HookPhase.AFTER_EACH_TEST)
            val afterHooksId = RunnerIdCreator.getAfterTestHookContainerId(testId)

            val beforeEachHooks = parentNodeAsHooksContainer.beforeEachStepCalls.mapIndexed {idx, it -> createStepCallBranch(it, idx, beforeEachId)}
            val afterEachHooks = parentNodeAsHooksContainer.afterEachStepCalls.mapIndexed {idx, it -> createStepCallBranch(it, idx, afterEachId)}

            val afterHooks = testPathAndModel.model.afterHooks.mapIndexed {idx, it -> createStepCallBranch(it, idx, afterHooksId)}
            val testPath = testPathAndModel.model.path

            val runnerTestNode = RunnerTestNode(
                id = testId,
                name = testPathAndModel.model.name,
                path = testPath,
                enabled = !testPathAndModel.model.properties.isDisabled,
                beforeEachHooks = RunnerHooksNode(beforeEachId, BEFORE_EACH_HOOKS, testPath, beforeEachHooks),
                afterEachHooks = RunnerHooksNode(afterEachId, AFTER_EACH_HOOKS, testPath, afterEachHooks),
                afterHooks = RunnerHooksNode(afterHooksId, AFTER_HOOKS, testPath, afterHooks)
            )

            val stepCalls: List<RunnerStepNode> = testPathAndModel.model.stepCalls.mapIndexed {idx, it ->
                createStepCallBranch(it, idx, runnerTestNode.id)
            }
            stepCalls.forEach {runnerTestNode.addChild(it)}

            return runnerTestNode
        }
    }

    private fun createTestScenarioBranch(parentNode: ContainerTreeNode,
                                         parentHooksContainer: RunnerHooksContainerNode,
                                         test: TestModel,
                                         scenarioWithOriginalIndex: Pair<Int, Scenario>,
                                         filteredScenarioIndex: Int): RunnerScenarioNode {
        val originalScenarioIndex = scenarioWithOriginalIndex.first
        val scenario = scenarioWithOriginalIndex.second

        val scenarioId = RunnerIdCreator.getScenarioId((parentNode as RunnerNode).id, filteredScenarioIndex, test)
        val beforeEachId = RunnerIdCreator.getHookContainerId(scenarioId, HookPhase.BEFORE_EACH_TEST)
        val afterEachId = RunnerIdCreator.getHookContainerId(scenarioId, HookPhase.AFTER_EACH_TEST)
        val afterHooksId = RunnerIdCreator.getAfterTestHookContainerId(scenarioId)

        val beforeEachHooks = parentHooksContainer.beforeEachStepCalls.mapIndexed {idx, it -> createStepCallBranch(it, idx, beforeEachId)}
        val afterEachHooks = parentHooksContainer.afterEachStepCalls.mapIndexed {idx, it -> createStepCallBranch(it, idx, afterEachId)}
        
        val stepCalls: List<RunnerStepNode> = test.stepCalls.mapIndexed {index, it -> createStepCallBranch(it, index, scenarioId)}
        val afterHooks = test.afterHooks.mapIndexed {index, it -> createStepCallBranch(it, index, afterHooksId)}
        val scenarioPath = test.path

        val runnerScenarioNode = RunnerScenarioNode(
            id = scenarioId,
            path = scenarioPath,
            scenarioIndex = filteredScenarioIndex,
            name = scenario.name ?: "Scenario ${originalScenarioIndex + 1}",
            enabled = scenario.enabled,
            beforeEachHooks = RunnerHooksNode(beforeEachId, BEFORE_EACH_HOOKS, scenarioPath, beforeEachHooks),
            afterEachHooks = RunnerHooksNode(afterEachId, AFTER_EACH_HOOKS, scenarioPath, afterEachHooks),
            afterHooks = RunnerHooksNode(afterHooksId, AFTER_HOOKS, scenarioPath, afterHooks)
        )

        stepCalls.forEach { runnerScenarioNode.addChild(it) }
        return runnerScenarioNode
    }

    private fun createStepCallBranch(stepCall: StepCall, indexInParent: Int, parentId: String): RunnerStepNode {
        val stepDef = stepCall.stepDef
        val id = RunnerIdCreator.getStepId(parentId, indexInParent, stepCall)
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
                val childrenNodes: List<RunnerStepNode> = stepDef.stepCalls.mapIndexed {idx, it -> createStepCallBranch(it, idx, id)}

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
