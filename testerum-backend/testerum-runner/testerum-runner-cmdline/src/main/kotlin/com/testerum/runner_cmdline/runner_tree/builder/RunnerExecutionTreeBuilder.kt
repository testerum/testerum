package com.testerum.runner_cmdline.runner_tree.builder

import com.testerum.file_service.caches.resolved.BasicStepsCache
import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.model.test.scenario.Scenario
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.tests_finder.TestPath
import com.testerum.model.tests_finder.TestsFinder
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.project_manager.RunnerProjectManager
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
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

class RunnerExecutionTreeBuilder(private val runnerProjectManager: RunnerProjectManager,
                                 private val basicStepsCache: BasicStepsCache,
                                 private val executionName: String?) {

    companion object {
        private val LOG = LoggerFactory.getLogger(RunnerExecutionTreeBuilder::class.java)
    }

    fun createTree(cmdlineParams: CmdlineParams,
                   testsDir: JavaPath): RunnerSuite {
        // get hooks
        val hooks: Collection<HookDef> = basicStepsCache.getHooks()

        val testsDirectoryRoot = testsDir.toAbsolutePath()
        val testsMap = TestsFinder.loadTestsToRun(
                testPaths = cmdlineParams.testPaths,
                tagsToInclude = cmdlineParams.tagsToInclude,
                tagsToExclude = cmdlineParams.tagsToExclude,
                testsDirectoryRoot = testsDirectoryRoot,
                loadTestAtPath = { runnerProjectManager.getProjectServices().getTestsCache().getTestAtPath(it) }
        )
        val tests = testsMap.map { (path, test) -> TestWithFilePath(test, path) }
        val features = loadFeatures(tests.map { it.testPath.javaPath }, testsDirectoryRoot)

        val builder = TreeBuilder(
                customizer = RunnerExecutionTreeBuilderCustomizer(hooks, executionName)
        )
        features.forEach { builder.add(it) }
        tests.forEach { builder.add(it) }

        return builder.build() as RunnerSuite
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

    private data class TestWithFilePath(val test: TestModel, val testPath: TestPath)

    private class RunnerExecutionTreeBuilderCustomizer(hooks: Collection<HookDef>,
                                                       private val executionName: String?) : TreeBuilderCustomizer {

        private val beforeEachTestHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.BEFORE_EACH_TEST)
        private val afterEachTestHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.AFTER_EACH_TEST)
        private val beforeAllTestsHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.BEFORE_ALL_TESTS)
        private val afterAllTestsHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.AFTER_ALL_TESTS)

        override fun getPath(payload: Any): List<String> = when (payload) {
            is Feature          -> payload.path.parts
            is TestWithFilePath -> payload.test.path.parts
            else                -> throw unknownPayloadException(payload)
        }

        override fun isContainer(payload: Any): Boolean = when (payload) {
            is Feature          -> true
            is TestWithFilePath -> false
            else                -> throw unknownPayloadException(payload)
        }

        override fun getRootLabel(): String = buildString {
            append("Suite")
            if (executionName != null) {
                append(" - ").append(executionName)
            }
        }

        override fun getLabel(payload: Any): String = when (payload) {
            is Feature          -> payload.path.directories.last()
            is TestWithFilePath -> payload.test.name
            else                -> throw unknownPayloadException(payload)
        }

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<RunnerFeatureOrTest> = childrenNodes as List<RunnerFeatureOrTest>

            return RunnerSuite(
                    beforeAllTestsHooks = beforeAllTestsHooks,
                    featuresOrTests = children,
                    afterAllTestsHooks = afterAllTestsHooks,
                    executionName = executionName
            )
        }

        override fun createNode(payload: Any?, label: String, path: List<String>, childrenNodes: List<Any>, indexInParent: Int): Any {
            @Suppress("UNCHECKED_CAST")
            return when (payload) {
                null -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<RunnerFeatureOrTest> = childrenNodes as List<RunnerFeatureOrTest>

                    RunnerFeature(
                            featurePathFromRoot = path,
                            featureName = label,
                            tags = emptyList(),
                            featuresOrTests = children,
                            indexInParent = indexInParent
                    )
                }
                is Feature -> {
                    @Suppress("UNCHECKED_CAST")
                    val children: List<RunnerFeatureOrTest> = childrenNodes as List<RunnerFeatureOrTest>

                    RunnerFeature(
                            featurePathFromRoot = path,
                            featureName = label,
                            tags = payload.tags,
                            featuresOrTests = children,
                            indexInParent = indexInParent
                    )
                }
                is TestWithFilePath -> {
                    val isParametrizedTest = payload.test.scenarios.isNotEmpty()

                    if (isParametrizedTest) {
                        val testScenarios: List<RunnerScenario> = payload.test.scenarios.mapIndexed { index, scenario ->
                            createTestScenarioBranch(
                                    test = payload.test,
                                    filePath = payload.testPath.javaPath,
                                    scenarioIndex = index,
                                    scenario = scenario,
                                    beforeEachTestHooks = beforeEachTestHooks,
                                    afterEachTestHooks = afterEachTestHooks
                            )
                        }

                        val filteredTestScenarios = if (payload.testPath is ScenariosTestPath) {
                            // verify filter criteria
                            for (scenarioIndex in payload.testPath.scenarioIndexes) {
                                if (scenarioIndex >= testScenarios.size) {
                                    LOG.warn("invalid scenario index [$scenarioIndex] for test at [${payload.testPath.testFile}]: this test has only ${testScenarios.size} scenarios; the index must be between 0 and ${testScenarios.size - 1} inclusive")
                                }
                            }

                            // filter scenarios
                            if (payload.testPath.scenarioIndexes.isEmpty()) {
                                // there is no filter on scenarios
                                testScenarios
                            } else {
                                testScenarios.filterIndexed { scenarioIndex, _ ->
                                    scenarioIndex in payload.testPath.scenarioIndexes
                                }
                            }
                        } else {
                            testScenarios
                        }

                        RunnerParametrizedTest(
                                test = payload.test,
                                filePath = payload.testPath.javaPath,
                                indexInParent = indexInParent,
                                scenarios = filteredTestScenarios
                        )
                    } else {
                        if (payload.testPath is ScenariosTestPath) {
                            LOG.warn("the test at [${payload.testPath.testFile}] is nor parametrized, so specifying which scenarios to run has no effect (got scenarioIndexes=${payload.testPath.scenarioIndexes})")
                        }

                        createRunnerTest(
                                test = payload.test,
                                filePath = payload.testPath.javaPath,
                                testIndexInParent = indexInParent,
                                beforeEachTestHooks = beforeEachTestHooks,
                                afterEachTestHooks = afterEachTestHooks
                        )
                    }
                }
                else -> throw unknownPayloadException(payload)
            }
        }

        private fun createTestScenarioBranch(test: TestModel,
                                             filePath: JavaPath,
                                             scenarioIndex: Int,
                                             scenario: Scenario,
                                             beforeEachTestHooks: List<RunnerHook>,
                                             afterEachTestHooks: List<RunnerHook>): RunnerScenario {
            val runnerSteps = mutableListOf<RunnerStep>()

            for ((stepIndexInParent, stepCall) in test.stepCalls.withIndex()) {
                runnerSteps += createRunnerStep(stepCall, stepIndexInParent)
            }

            return RunnerScenario(
                    beforeEachTestHooks = beforeEachTestHooks,
                    test = test,
                    scenario = scenario,
                    scenarioIndex = scenarioIndex,
                    filePath = filePath,
                    steps = runnerSteps,
                    afterEachTestHooks = afterEachTestHooks
            )
        }

        private fun createRunnerTest(test: TestModel,
                                     filePath: JavaPath,
                                     testIndexInParent: Int,
                                     beforeEachTestHooks: List<RunnerHook>,
                                     afterEachTestHooks: List<RunnerHook>): RunnerTest {
            val runnerSteps = mutableListOf<RunnerStep>()

            for ((stepIndexInParent, stepCall) in test.stepCalls.withIndex()) {
                runnerSteps += createRunnerStep(stepCall, stepIndexInParent)
            }

            return RunnerTest(
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
                is BasicStepDef     -> RunnerBasicStep(stepCall, indexInParent)
                is ComposedStepDef  -> {
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

}
