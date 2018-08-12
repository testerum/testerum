package com.testerum.runner_cmdline.runner_tree.builder

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.repository.enums.FileType
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
import com.testerum.service.hooks.HooksService
import com.testerum.service.tests.TestsService

class RunnerExecutionTreeBuilder(private val runnerTestsFinder: RunnerTestsFinder,
                                 private val hooksService: HooksService,
                                 private val testsService: TestsService) {

    fun createTree(cmdlineParams: CmdlineParams): RunnerSuite {
        // get hooks
        val hooks: List<HookDef> = hooksService.getHooks()

        val testsDirectoryRoot = cmdlineParams.repositoryDirectory
                .resolve(FileType.TEST.relativeRootDirectory.toJavaPath())
                .toAbsolutePath()
        val builder = TreeBuilder(
                customizer = RunnerExecutionTreeBuilderCustomizer(testsService, hooks, testsDirectoryRoot)
        )

        // get tests
        val pathsToTestsToExecute: List<java.nio.file.Path> = runnerTestsFinder.findPathsToTestsToExecute(cmdlineParams)
        for (path in pathsToTestsToExecute) {
            builder.add(path)
        }

        return builder.build() as RunnerSuite
    }

    private class RunnerExecutionTreeBuilderCustomizer(private val testsService: TestsService,
                                                       hooks: List<HookDef>,
                                                       private val testsDirectoryRoot: java.nio.file.Path) : TreeBuilderCustomizer {

        private val beforeEachTestHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.BEFORE_EACH_TEST)
        private val afterEachTestHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.AFTER_EACH_TEST)
        private val beforeAllTestsHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.BEFORE_ALL_TESTS)
        private val afterAllTestsHooks: List<RunnerHook> = hooks.sortedHooksForPhase(HookPhase.AFTER_ALL_TESTS)

        private val testsByPath = mutableMapOf<java.nio.file.Path, TestModel>()

        private fun getTestByPath(path: java.nio.file.Path): TestModel {
            return testsByPath.getOrPut(path) {
                loadTest(path)
            }
        }

        override fun getPath(payload: Any): List<String> = when (payload) {
            is java.nio.file.Path -> getTestByPath(payload).path.parts
            else                  -> throw unknownPayloadException(payload)
        }

        override fun isContainer(payload: Any): Boolean = when (payload) {
            is java.nio.file.Path -> false
            else                  -> throw unknownPayloadException(payload)
        }

        override fun getRootLabel(): String = "Suite"

        override fun getLabel(payload: Any): String = when (payload) {
            is java.nio.file.Path -> getTestByPath(payload).text
            else                  -> throw unknownPayloadException(payload)
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
                is java.nio.file.Path -> {
                    val test = getTestByPath(payload)

                    createRunnerTest(
                            test,
                            payload,
                            testIndexInParent = indexInParent,
                            beforeEachTestHooks = beforeEachTestHooks,
                            afterEachTestHooks = afterEachTestHooks
                    )
                }
                else -> throw unknownPayloadException(payload)
            }
        }

        private fun List<HookDef>.sortedHooksForPhase(phase: HookPhase): List<RunnerHook>
                = this.asSequence()
                      .filter { it.phase == phase }
                      .sortedBy { it.order }
                      .map { RunnerHook(it) }
                      .toList()

        private fun loadTest(testPath: java.nio.file.Path): TestModel {
            try {
                val relativeTestPath = testsDirectoryRoot.relativize(testPath)
                val testerumPath = Path.createInstance(relativeTestPath.toString())

                return testsService.getTestAtPath(testerumPath)
                        ?: throw RuntimeException("could not find test at [${testPath.toAbsolutePath().normalize()}]")
            } catch (e: Exception) {
                throw RuntimeException("failed to load test at [${testPath.toAbsolutePath().normalize()}]", e)
            }
        }

        private fun createRunnerTest(test: TestModel,
                                     filePath: java.nio.file.Path,
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

    }

}
