package com.testerum.runner_cmdline.runner_tree.builder

import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.model.test.scenario.Scenario
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.tests_finder.TestsFinder
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.PathBasedTreeBuilder
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.model.util.new_tree_builder.TreeNodeFactory
import com.testerum.runner_cmdline.cmdline.params.model.RunCmdlineParams
import com.testerum.runner_cmdline.project_manager.RunnerProjectManager
import com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test.RunnerParametrizedTest
import com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test.RunnerScenario
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerBasicStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerComposedStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerUndefinedStep
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite
import com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTest
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import org.slf4j.LoggerFactory
import java.nio.file.Path as JavaPath

class RunnerExecutionTreeBuilder(
    private val runnerProjectManager: RunnerProjectManager,
    private val executionName: String?
) {

    private val stepsCache: StepsCache
        get() = runnerProjectManager.getProjectServices().getStepsCache()

    fun createTree(
        cmdlineParams: RunCmdlineParams,
        testsDir: JavaPath
    ): RunnerSuite {
        // get hooks
        val hooks: Collection<HookDef> = stepsCache.getHooks()

        val testsDirectoryRoot = testsDir.toAbsolutePath()
        val testsMap = TestsFinder.loadTestsToRun(
            testPaths = cmdlineParams.testPaths,
            tagsToInclude = cmdlineParams.includeTags,
            tagsToExclude = cmdlineParams.excludeTags,
            testsDirectoryRoot = testsDirectoryRoot,
            loadTestAtPath = { runnerProjectManager.getProjectServices().getTestsCache().getTestAtPath(it) }
        )
        val tests = testsMap.map { (path, test) -> TestWithFilePath(test, path) }
        val features = loadFeatures(tests.map { it.testPath.javaPath }, testsDirectoryRoot)

        val glueClassNames = getGlueClassNames(hooks, tests, features)

        val items = mutableListOf<HasPath>()
        items += tests
        items += features

        val treeBuilder = PathBasedTreeBuilder(
            RunnerTreeNodeFactory(hooks, executionName, glueClassNames)
        )

        return treeBuilder.createTree(items)
    }

    private class RunnerTreeNodeFactory(
        hooks: Collection<HookDef>,
        private val executionName: String?,
        private val glueClassNames: List<String>,
    ) : TreeNodeFactory<RunnerSuite, RunnerFeature> {

        companion object {
            private val LOG = LoggerFactory.getLogger(RunnerExecutionTreeBuilder::class.java)
        }

        private val beforeEachTestHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.BEFORE_EACH_TEST)
        private val afterEachTestHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.AFTER_EACH_TEST)
        private val beforeAllTestsHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.BEFORE_ALL_TESTS)
        private val afterAllTestsHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.AFTER_ALL_TESTS)

        override fun createRootNode(): RunnerSuite {
            return RunnerSuite(beforeAllTestsHooks, afterAllTestsHooks, executionName, glueClassNames)
        }

        override fun createVirtualContainer(parentNode: ContainerTreeNode, path: Path): RunnerFeature {
            return RunnerFeature(
                parent = parentNode,
                featurePathFromRoot = path.directories,
                featureName = path.directories.last(),
                tags = emptyList(),
                indexInParent = parentNode.childrenCount
            )
        }

        override fun createNode(parentNode: ContainerTreeNode, item: HasPath): TreeNode {
            val indexInParent = parentNode.childrenCount

            return when (item) {
                is Feature -> {
                    RunnerFeature(
                        parent = parentNode,
                        featurePathFromRoot = item.path.directories,
                        featureName = item.name,
                        tags = item.tags,
                        indexInParent = parentNode.childrenCount
                    )
                }
                is TestWithFilePath -> {
                    val isParametrizedTest = item.test.scenarios.isNotEmpty()

                    if (isParametrizedTest) {
                        createParametrizedTest(item, parentNode, indexInParent)
                    } else {
                        createTest(item, parentNode, indexInParent)
                    }
                }
                else -> throw IllegalArgumentException("unexpected item type [${item.javaClass}]: [$item]")
            }
        }

        private fun createParametrizedTest(
            item: TestWithFilePath,
            parentNode: ContainerTreeNode,
            indexInParent: Int
        ): RunnerParametrizedTest {
            verifyScenarioIndexesFromFilter(item)

            val filteredTestScenarios = filterScenarios(item)

            val runnerScenariosNodes: List<RunnerScenario> = filteredTestScenarios.mapIndexed { filteredScenarioIndex, scenarioWithOriginalIndex ->
                createTestScenarioBranch(
                    test = item.test,
                    filePath = item.testPath.javaPath,
                    scenarioWithOriginalIndex = scenarioWithOriginalIndex,
                    filteredScenarioIndex = filteredScenarioIndex,
                    beforeEachTestHooks = beforeEachTestHooks,
                    afterEachTestHooks = afterEachTestHooks
                )
            }

            return RunnerParametrizedTest(
                parent = parentNode,
                test = item.test,
                filePath = item.testPath.javaPath,
                indexInParent = indexInParent,
                scenarios = runnerScenariosNodes
            )
        }

        private fun createTest(
            item: TestWithFilePath,
            parentNode: ContainerTreeNode,
            indexInParent: Int
        ): RunnerTest {
            if (item.testPath is ScenariosTestPath) {
                LOG.warn(
                    "the test at [${item.testPath.testFile}] is nor parametrized," +
                        " so specifying which scenarios to run has no effect" +
                        " (got scenarioIndexes=${item.testPath.scenarioIndexes})"
                )
            }

            return createRunnerTest(
                parentNode = parentNode,
                test = item.test,
                filePath = item.testPath.javaPath,
                testIndexInParent = indexInParent,
                beforeEachTestHooks = beforeEachTestHooks,
                afterEachTestHooks = afterEachTestHooks
            )
        }

        private fun filterScenarios(item: TestWithFilePath): List<Pair<Int, Scenario>> {
            val scenariosWithOriginalIndex = item.test.scenarios.mapIndexed { index, scenario ->
                index to scenario
            }

            val filteredTestScenarios = if (item.testPath is ScenariosTestPath) {
                // filter scenarios
                if (item.testPath.scenarioIndexes.isEmpty()) {
                    // there is no filter on scenarios
                    scenariosWithOriginalIndex
                } else {
                    scenariosWithOriginalIndex.filterIndexed { scenarioIndex, _ ->
                        scenarioIndex in item.testPath.scenarioIndexes
                    }
                }
            } else {
                scenariosWithOriginalIndex
            }
            return filteredTestScenarios
        }

        private fun verifyScenarioIndexesFromFilter(item: TestWithFilePath) {
            if (item.testPath !is ScenariosTestPath) {
                return
            }

            for (scenarioIndex in item.testPath.scenarioIndexes) {
                if ((scenarioIndex < 0) || (scenarioIndex >= item.test.scenarios.size)) {
                    LOG.warn(
                        "invalid scenario index [$scenarioIndex]" +
                            " for test at [${item.testPath.testFile}]" +
                            ": this test has only ${item.test.scenarios.size} scenarios" +
                            "; the index must be between 0 and ${item.test.scenarios.size - 1} inclusive"
                    )
                }
            }
        }

        private fun createTestScenarioBranch(
            test: TestModel,
            filePath: java.nio.file.Path,
            scenarioWithOriginalIndex: Pair<Int, Scenario>,
            filteredScenarioIndex: Int,
            beforeEachTestHooks: List<RunnerHook>,
            afterEachTestHooks: List<RunnerHook>
        ): RunnerScenario {
            val originalScenarioIndex = scenarioWithOriginalIndex.first
            val scenario = scenarioWithOriginalIndex.second

            val runnerSteps = mutableListOf<RunnerStep>()

            for ((stepIndexInParent, stepCall) in test.stepCalls.withIndex()) {
                runnerSteps += createRunnerStep(stepCall, stepIndexInParent)
            }

            return RunnerScenario(
                beforeEachTestHooks = beforeEachTestHooks,
                test = test,
                scenario = scenario,
                originalScenarioIndex = originalScenarioIndex,
                filteredScenarioIndex = filteredScenarioIndex,
                filePath = filePath,
                steps = runnerSteps,
                afterEachTestHooks = afterEachTestHooks
            )
        }

        private fun createRunnerTest(
            parentNode: ContainerTreeNode,
            test: TestModel,
            filePath: java.nio.file.Path,
            testIndexInParent: Int,
            beforeEachTestHooks: List<RunnerHook>,
            afterEachTestHooks: List<RunnerHook>
        ): RunnerTest {
            val runnerSteps = mutableListOf<RunnerStep>()

            for ((stepIndexInParent, stepCall) in test.stepCalls.withIndex()) {
                runnerSteps += createRunnerStep(stepCall, stepIndexInParent)
            }

            return RunnerTest(
                parent = parentNode,
                test = test,
                filePath = filePath,
                indexInParent = testIndexInParent,
                steps = runnerSteps,
                beforeEachTestHooks = beforeEachTestHooks,
                afterEachTestHooks = afterEachTestHooks
            )
        }


        private fun createRunnerStep(stepCall: StepCall, indexInParent: Int): RunnerStep {
            val stepDef = stepCall.stepDef

            return when (stepDef) {
                is UndefinedStepDef -> RunnerUndefinedStep(stepCall, indexInParent)
                is BasicStepDef -> RunnerBasicStep(stepCall, indexInParent)
                is ComposedStepDef -> {
                    val nestedSteps = mutableListOf<RunnerStep>()

                    for ((nestedIndexInParent, nestedStepCall) in stepDef.stepCalls.withIndex()) {
                        val nestedRunnerStep = createRunnerStep(nestedStepCall, nestedIndexInParent)

                        nestedSteps += nestedRunnerStep
                    }

                    RunnerComposedStep(
                        stepCall = stepCall,
                        indexInParent = indexInParent,
                        steps = nestedSteps
                    )
                }
                else -> throw RuntimeException("unknown StepDef type [${stepDef.javaClass.name}]")
            }
        }
        private fun Collection<HookDef>.sortedHooksForPhase(phase: HookPhase): List<RunnerHook> {
            return this.asSequence()
                .filter { it.phase == phase }
                .sortedBy { it.order }
                .map { RunnerHook(it) }
                .toList()
        }
    }

    private fun getGlueClassNames(
        basicHookDefs: Collection<HookDef>,
        testsWithPaths: List<TestWithFilePath>,
        features: List<Feature>
    ): List<String> {
        val result = mutableListOf<String>()

        for (basicHookDef in basicHookDefs) {
            result += basicHookDef.className
        }

        for (testWithPath in testsWithPaths) {
            val test = testWithPath.test

            for (stepCall in test.afterHooks) {
                addGlueClassNames(result, stepCall)
            }

            for (stepCall in test.stepCalls) {
                addGlueClassNames(result, stepCall)
            }
        }

        for (feature in features) {
            for (stepCall in feature.hooks.beforeAll) {
                addGlueClassNames(result, stepCall)
            }
            for (stepCall in feature.hooks.beforeEach) {
                addGlueClassNames(result, stepCall)
            }
            for (stepCall in feature.hooks.afterEach) {
                addGlueClassNames(result, stepCall)
            }
            for (stepCall in feature.hooks.afterAll) {
                addGlueClassNames(result, stepCall)
            }
        }

        return result
    }

    private fun addGlueClassNames(
        result: MutableList<String>,
        stepCall: StepCall
    ) {
        when (val stepDef = stepCall.stepDef) {
            is BasicStepDef -> result += stepDef.className
            is ComposedStepDef -> {
                for (childStepCall in stepDef.stepCalls) {
                    addGlueClassNames(result, childStepCall)
                }
            }
        }
    }

    private fun loadFeatures(testJavaPaths: List<JavaPath>, testsDirectoryRoot: JavaPath): List<Feature> {
        val result = arrayListOf<Feature>()

        val possibleFeaturePaths = getPossibleFeaturePaths(testJavaPaths, testsDirectoryRoot)
        for (possibleFeaturePath in possibleFeaturePaths) {
            val feature = runnerProjectManager.getProjectServices().getFeatureCache().getFeatureAtPath(possibleFeaturePath)
                ?: continue

            result += feature
        }

        return result
    }

    private fun getPossibleFeaturePaths(testJavaPaths: List<JavaPath>, testsDirectoryRoot: JavaPath): List<Path> {
        val result = linkedSetOf<String>()

        for (testJavaPath in testJavaPaths) {
            val relativeTestJavaPath = testsDirectoryRoot.relativize(testJavaPath)
            val testPath = Path.createInstance(relativeTestJavaPath.toString())

            val testPathDirectories = testPath.directories
            for (i in 1..testPathDirectories.size) {
                result += testPathDirectories.subList(0, i)
                    .joinToString(separator = "/")
            }
        }

        return result.map {
            Path.createInstance(it)
        }
    }


}
