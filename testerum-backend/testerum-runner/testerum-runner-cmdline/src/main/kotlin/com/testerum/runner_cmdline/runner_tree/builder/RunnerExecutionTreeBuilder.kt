package com.testerum.runner_cmdline.runner_tree.builder

import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.model.feature.Feature
import com.testerum.model.feature.hooks.HookPhase
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
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature
import com.testerum.runner_cmdline.runner_tree.nodes.hook.FeatureHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.hook.HookSource
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBasicHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerComposedHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.SuiteHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.hook.TestHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test.RunnerParametrizedTest
import com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test.RunnerScenario
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerBasicStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerComposedStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerUndefinedStep
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite
import com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTest
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
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

        private val beforeEachTestBasicHooks: List<RunnerBasicHook> = hooks.sortedBasicHooksForPhase(HookPhase.BEFORE_EACH_TEST)
        private val afterEachTestBasicHooks: List<RunnerBasicHook> = hooks.sortedBasicHooksForPhase(HookPhase.AFTER_EACH_TEST)
        private val beforeAllTestsBasicHooks: List<RunnerBasicHook> = hooks.sortedBasicHooksForPhase(HookPhase.BEFORE_ALL_TESTS)
        private val afterAllTestsBasicHooks: List<RunnerBasicHook> = hooks.sortedBasicHooksForPhase(HookPhase.AFTER_ALL_TESTS)

        override fun createRootNode(item: HasPath?): RunnerSuite {
            val rootFeature = item as? Feature
                ?: throw IllegalStateException(
                    "INTERNAL ERROR: root item is either null or not a ${Feature::class.simpleName}" +
                        ": itemClass=[${item?.javaClass}]" +
                        ", item=[$item]"
                )

            val runnerSuite = RunnerSuite(executionName, glueClassNames, rootFeature)

            var childrenCount = 0

            // hooks: basic before-all
            for (hook in beforeAllTestsBasicHooks) {
                runnerSuite.addBeforeAllHook(hook) // todo: don't we need the correct indexInParent for basic hooks? (not for now, since these are not nodes in the tree)
                childrenCount++
            }

            // hooks: composed before-all
            for (hookStepCall in rootFeature.hooks.beforeAll) {
                runnerSuite.addBeforeAllHook(
                    createRunnerComposedHook(
                        parent = runnerSuite,
                        indexInParent = childrenCount,
                        stepCall = hookStepCall,
                        phase = HookPhase.BEFORE_ALL_TESTS,
                        source = SuiteHookSource
                    )
                )
                childrenCount++
            }

            // hooks: composed after-all
            for (hookStepCall in rootFeature.hooks.afterAll) {
                runnerSuite.addAfterAllHook(
                    createRunnerComposedHook(
                        parent = runnerSuite,
                        indexInParent = childrenCount,
                        stepCall = hookStepCall,
                        phase = HookPhase.AFTER_ALL_TESTS,
                        source = SuiteHookSource
                    )
                )
                childrenCount++
            }

            // hooks: basic after-all
            for (hook in afterAllTestsBasicHooks) {
                runnerSuite.addAfterAllHook(hook) // todo: don't we need the correct indexInParent for basic hooks? (not for now, since these are not nodes in the tree)
                childrenCount++
            }

            return runnerSuite
        }

        override fun createVirtualContainer(parentNode: ContainerTreeNode, path: Path): RunnerFeature {
            return RunnerFeature(
                parent = parentNode,
                indexInParent = parentNode.childrenCount,
                featurePathFromRoot = path.directories,
                featureName = path.directories.last(),
                tags = emptyList(),
                feature = Feature(
                    name = "",
                    path = Path.EMPTY,
                )
            )
        }

        override fun createNode(parentNode: ContainerTreeNode, item: HasPath): TreeNode {
            if (parentNode !is RunnerTreeNode) {
                throw IllegalStateException("unexpected parent type: [${parentNode.javaClass}]: [$parentNode]")
            }
            val indexInParent = parentNode.childrenCount

            return when (item) {
                is Feature -> {
                    val feature = RunnerFeature(
                        parent = parentNode,
                        indexInParent = parentNode.childrenCount,
                        featurePathFromRoot = item.path.directories,
                        featureName = item.name,
                        tags = item.tags,
                        feature = item
                    )

                    var childrenCount = 0

                    // hooks: composed before-all
                    for (hookStepCall in item.hooks.beforeAll) {
                        feature.addBeforeAllHook(
                            createRunnerComposedHook(
                                parent = feature,
                                indexInParent = childrenCount,
                                stepCall = hookStepCall,
                                phase = HookPhase.BEFORE_ALL_TESTS,
                                source = FeatureHookSource(item.path)
                            )
                        )
                        childrenCount++
                    }

                    // hooks: composed after-all
                    for (hookStepCall in item.hooks.afterAll) {
                        feature.addAfterAllHook(
                            createRunnerComposedHook(
                                parent = feature,
                                indexInParent = childrenCount,
                                stepCall = hookStepCall,
                                phase = HookPhase.AFTER_ALL_TESTS,
                                source = FeatureHookSource(item.path)
                            )
                        )
                        childrenCount++
                    }

                    feature
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
                    parentNode = parentNode,
                    test = item.test,
                    filePath = item.testPath.javaPath,
                    scenarioWithOriginalIndex = scenarioWithOriginalIndex,
                    filteredScenarioIndex = filteredScenarioIndex,
                    beforeEachTestBasicHooks = beforeEachTestBasicHooks,
                    afterEachTestBasicHooks = afterEachTestBasicHooks
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

        private fun <P> createTest(
            item: TestWithFilePath,
            parentNode: P,
            indexInParent: Int
        ): RunnerTest where P : ContainerTreeNode, P : RunnerTreeNode {
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
            parentNode: ContainerTreeNode,
            test: TestModel,
            filePath: JavaPath,
            scenarioWithOriginalIndex: Pair<Int, Scenario>,
            filteredScenarioIndex: Int,
            beforeEachTestBasicHooks: List<RunnerBasicHook>,
            afterEachTestBasicHooks: List<RunnerBasicHook>
        ): RunnerScenario {
            val originalScenarioIndex = scenarioWithOriginalIndex.first
            val scenario = scenarioWithOriginalIndex.second

            val runnerSteps = mutableListOf<RunnerStep>()

            for ((stepIndexInParent, stepCall) in test.stepCalls.withIndex()) {
                runnerSteps += createRunnerStep(parentNode, stepIndexInParent, stepCall, logEvents = true)
            }

            return RunnerScenario(
                parent = parentNode,
                beforeEachTestBasicHooks = beforeEachTestBasicHooks,
                test = test,
                scenario = scenario,
                originalScenarioIndex = originalScenarioIndex,
                filteredScenarioIndex = filteredScenarioIndex,
                filePath = filePath,
                steps = runnerSteps,
                afterEachTestBasicHooks = afterEachTestBasicHooks
            )
        }

        private fun <P> createRunnerTest(
            parentNode: P,
            test: TestModel,
            filePath: JavaPath,
            testIndexInParent: Int,
        ): RunnerTest where P : ContainerTreeNode, P : RunnerTreeNode {
            val runnerTest = RunnerTest(
                parent = parentNode,
                test = test,
                filePath = filePath,
                indexInParent = testIndexInParent,
            )
            var beforeHooksCount = 0
            var afterHooksCount = 0

            // before hooks: basic before-each
            for (hook in beforeEachTestBasicHooks) {
                runnerTest.addBeforeEachHook(hook)
                beforeHooksCount++
            }

            // before hooks: parent features before-each
            val beforeEachComposedHooks = getInheritedHooks(
                parentForHooks = runnerTest,
                phase = HookPhase.BEFORE_EACH_TEST,
                descendingFromRoot = true
            )
            for (hook in beforeEachComposedHooks) {
                runnerTest.addBeforeEachHook(hook)
                beforeHooksCount++
            }

            // test steps
            for ((stepIndexInParent, stepCall) in test.stepCalls.withIndex()) {
                runnerTest.addChild(
                    createRunnerStep(runnerTest, stepIndexInParent, stepCall, logEvents = true)
                )
            }

            //  hooks: test after
            for (hookStepCall in test.afterHooks) {
                val runnerComposedHook = RunnerComposedHook(
                    parent = runnerTest,
                    indexInParent = afterHooksCount,
                    phase = HookPhase.AFTER_EACH_TEST,
                    source = TestHookSource(testPath = test.path)
                )
                runnerComposedHook.setStep(
                    createRunnerStep(
                        parentNode = runnerComposedHook,
                        indexInParent = 0,
                        stepCall = hookStepCall,
                        logEvents = false
                    )
                )
                runnerTest.addAfterEachHook(runnerComposedHook)
                afterHooksCount++
            }

            // after hooks: parent features after-each
            val afterEachComposedHooks = getInheritedHooks(
                parentForHooks = runnerTest,
                phase = HookPhase.AFTER_EACH_TEST,
                descendingFromRoot = false
            )
            for (hook in afterEachComposedHooks) {
                runnerTest.addAfterEachHook(hook)
                afterHooksCount++
            }

            // after hooks: basic after-each
            for (hook in afterEachTestBasicHooks) {
                runnerTest.addAfterEachHook(hook)
                afterHooksCount++
            }

            return runnerTest
        }

        private fun createRunnerComposedHook(
            parent: TreeNode,
            indexInParent: Int,
            stepCall: StepCall,
            phase: HookPhase,
            source: HookSource,
        ): RunnerComposedHook {
            val runnerComposedHook = RunnerComposedHook(
                parent = parent,
                indexInParent = indexInParent,
                phase = phase,
                source = source
            )

            runnerComposedHook.setStep(
                createRunnerStep(
                    parentNode = runnerComposedHook,
                    indexInParent = 0,
                    stepCall = stepCall,
                    logEvents = false
                )
            )

            return runnerComposedHook
        }

        private fun createRunnerStep(
            parentNode: TreeNode,
            indexInParent: Int,
            stepCall: StepCall,
            logEvents: Boolean,
        ): RunnerStep {
            return when (val stepDef = stepCall.stepDef) {
                is UndefinedStepDef -> RunnerUndefinedStep(parentNode, stepCall, indexInParent, logEvents)
                is BasicStepDef -> RunnerBasicStep(parentNode, stepCall, indexInParent, logEvents)
                is ComposedStepDef -> {
                    val nestedSteps = mutableListOf<RunnerStep>()

                    for ((nestedIndexInParent, nestedStepCall) in stepDef.stepCalls.withIndex()) {
                        val nestedRunnerStep = createRunnerStep(parentNode, nestedIndexInParent, nestedStepCall, logEvents)

                        nestedSteps += nestedRunnerStep
                    }

                    RunnerComposedStep(
                        parent = parentNode,
                        stepCall = stepCall,
                        indexInParent = indexInParent,
                        steps = nestedSteps,
                        logEvents = logEvents
                    )
                }
                else -> throw RuntimeException("unknown StepDef type [${stepDef.javaClass.name}]")
            }
        }

        private fun Collection<HookDef>.sortedBasicHooksForPhase(phase: HookPhase): List<RunnerBasicHook> {
            return this.asSequence()
                .filter { it.phase == phase }
                .sortedBy { it.order }
                .map { RunnerBasicHook(it) }
                .toList()
        }

        private fun <P> getInheritedHooks(
            parentForHooks: P,
            phase: HookPhase,
            descendingFromRoot: Boolean,
        ): List<RunnerComposedHook> where P : TreeNode, P: RunnerTreeNode {
            val featuresOrSuite = mutableListOf<RunnerTreeNode>()

            var currentNode: RunnerTreeNode? = parentForHooks
            while (currentNode != null) {
                if (currentNode is RunnerFeature || currentNode is RunnerSuite) {
                    featuresOrSuite += currentNode
                }

                currentNode = currentNode.parent
            }

            // the list if sorted from leaf to root; reverse if needed
            if (descendingFromRoot) {
                featuresOrSuite.reverse()
            }

            val hooks = mutableListOf<RunnerComposedHook>()

            for (featureOrSuite in featuresOrSuite) {
                when (featureOrSuite) {
                    is RunnerFeature -> {
                        for (hookStepCall in featureOrSuite.feature.hooks.getByPhase(phase)) {
                            hooks += createRunnerComposedHook(
                                parent = parentForHooks,
                                indexInParent = hooks.size,
                                stepCall = hookStepCall,
                                phase = phase,
                                source = FeatureHookSource(featurePath = featureOrSuite.feature.path)
                            )
                        }
                    }
                    is RunnerSuite -> {
                        for (hookStepCall in featureOrSuite.feature.hooks.getByPhase(phase)) {
                            hooks += createRunnerComposedHook(
                                parent = parentForHooks,
                                indexInParent = hooks.size,
                                stepCall = hookStepCall,
                                phase = phase,
                                source = SuiteHookSource
                            )
                        }
                    }
                }
            }

            return hooks
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

        // add virtual root feature if it doesn't exist (e.g. to get an empty list of hooks)
        val rootFeatureNotFound = result.none { it.path == Path.EMPTY }
        if (rootFeatureNotFound) {
            result += Feature(
                name = "",
                path = Path.EMPTY,
            )
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

        // add root feature
        // we need to load it because it may have hooks
        result += ""

        return result.map {
            Path.createInstance(it)
        }
    }

}
