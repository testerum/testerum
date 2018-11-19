package com.testerum.runner_cmdline.runner_tree.builder

import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.test.TestModel
import com.testerum.model.util.tree_builder.TreeBuilder
import com.testerum.model.util.tree_builder.TreeBuilderCustomizer
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerBasicStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerComposedStep
import com.testerum.runner_cmdline.runner_tree.nodes.step.impl.RunnerUndefinedStep
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite
import com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTest
import com.testerum.runner_cmdline.tests_finder.RunnerTestsFinder
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import java.nio.file.Path as JavaPath

class RunnerExecutionTreeBuilder(private val runnerTestsFinder: RunnerTestsFinder,
                                 private val stepsCache: StepsCache,
                                 private val testsCache: TestsCache) {

    //
    // VERY IMPORTANT!!!
    //
    // Changes to this class needs to be kept in sync with "com.testerum.model.runner.tree.builder.RunnerTreeBuilder"
    //

    fun createTree(cmdlineParams: CmdlineParams,
                   testsDir: JavaPath): RunnerSuite {
        // get hooks
        val hooks: Collection<HookDef> = stepsCache.getHooks()

        val testsDirectoryRoot = testsDir.toAbsolutePath()
        val pathsToTestsToExecute: List<JavaPath> = runnerTestsFinder.findPathsToTestsToExecute(cmdlineParams, testsDir)
        val tests = loadTests(pathsToTestsToExecute, testsDirectoryRoot)

        val builder = TreeBuilder(
                customizer = RunnerExecutionTreeBuilderCustomizer(hooks)
        )
        tests.forEach { builder.add(it) }

        return builder.build() as RunnerSuite
    }

    private fun loadTests(testPaths: List<JavaPath>, testsDirectoryRoot: JavaPath): List<TestWithFilePath> {
        val result = arrayListOf<TestWithFilePath>()

        for (testPath in testPaths) {
            val testWithFilePath: TestWithFilePath = loadTest(testPath, testsDirectoryRoot)

            // ignore manual tests
            if (testWithFilePath.test.properties.isManual) {
                continue
            }

            result += testWithFilePath
        }

        return result
    }

    private fun loadTest(testPath: JavaPath,
                         testsDirectoryRoot: JavaPath): TestWithFilePath {
        try {
            val relativeTestPath = testsDirectoryRoot.relativize(testPath)
            val testerumPath = Path.createInstance(relativeTestPath.toString())

            val test = testsCache.getTestAtPath(testerumPath)
                    ?: throw RuntimeException("could not find test at [${testPath.toAbsolutePath().normalize()}]")

            return TestWithFilePath(test, testPath)
        } catch (e: Exception) {
            throw RuntimeException("failed to load test at [${testPath.toAbsolutePath().normalize()}]", e)
        }
    }

    private data class TestWithFilePath(val test: TestModel, val filePath: JavaPath)

    private class RunnerExecutionTreeBuilderCustomizer(hooks: Collection<HookDef>) : TreeBuilderCustomizer {

        private val beforeEachTestHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.BEFORE_EACH_TEST)
        private val afterEachTestHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.AFTER_EACH_TEST)
        private val beforeAllTestsHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.BEFORE_ALL_TESTS)
        private val afterAllTestsHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.AFTER_ALL_TESTS)

        override fun getPath(payload: Any): List<String> = when (payload) {
            is TestWithFilePath -> payload.test.path.parts
            else                -> throw unknownPayloadException(payload)
        }

        override fun isContainer(payload: Any): Boolean = when (payload) {
            is TestWithFilePath -> false
            else                -> throw unknownPayloadException(payload)
        }

        override fun getRootLabel(): String = "Suite"

        override fun getLabel(payload: Any): String = when (payload) {
            is TestWithFilePath -> payload.test.name
            else                -> throw unknownPayloadException(payload)
        }

        override fun createRootNode(childrenNodes: List<Any>): Any {
            @Suppress("UNCHECKED_CAST")
            val children: List<RunnerFeatureOrTest> = childrenNodes as List<RunnerFeatureOrTest>

            return RunnerSuite(
                    beforeAllTestsHooks = beforeAllTestsHooks,
                    featuresOrTests = children,
                    afterAllTestsHooks = afterAllTestsHooks
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
                            featuresOrTests = children,
                            indexInParent = indexInParent
                    )
                }
                is TestWithFilePath -> {
                    createRunnerTest(
                            test = payload.test,
                            filePath = payload.filePath,
                            testIndexInParent = indexInParent,
                            beforeEachTestHooks = beforeEachTestHooks,
                            afterEachTestHooks = afterEachTestHooks
                    )
                }
                else -> throw unknownPayloadException(payload)
            }
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
