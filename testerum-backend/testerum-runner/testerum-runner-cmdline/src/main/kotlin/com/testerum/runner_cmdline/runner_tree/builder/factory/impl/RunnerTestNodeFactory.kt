package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.feature.hooks.HookPhase.AFTER_EACH_TEST
import com.testerum.model.feature.hooks.HookPhase.AFTER_TEST
import com.testerum.model.feature.hooks.HookPhase.BEFORE_EACH_TEST
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.runner_cmdline.runner_tree.builder.TestWithFilePath
import com.testerum.runner_cmdline.runner_tree.builder.factory.util.InheritedHooksFinder
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooks
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBasicHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooks
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerComposedHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.TestHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTest
import org.slf4j.LoggerFactory

object RunnerTestNodeFactory {
    private val LOG = LoggerFactory.getLogger(RunnerTestNodeFactory::class.java)

    fun <P> create(
        item: TestWithFilePath,
        parentNode: P,
        indexInParent: Int,
        beforeEachTestBasicHooks: List<RunnerBasicHook>,
        afterEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerTest where P : ContainerTreeNode, P : RunnerTreeNode {
        if (item.testPath is ScenariosTestPath) {
            LOG.warn(
                "the test at [${item.testPath.testFile}] is nor parametrized," +
                    " so specifying which scenarios to run has no effect" +
                    " (got scenarioIndexes=${item.testPath.scenarioIndexes})"
            )
        }

        val runnerTest = RunnerTest(
            parent = parentNode,
            test = item.test,
            filePath = item.testPath.javaPath,
            indexInParent = indexInParent,
        )

        runnerTest.setBeforeHooks(
            getBeforeHooks(runnerTest, beforeEachTestBasicHooks)
        )

        // test steps
        for ((stepIndexInParent, stepCall) in item.test.stepCalls.withIndex()) {
            runnerTest.addChild(
                RunnerStepNodeFactory.create(runnerTest, stepIndexInParent, stepCall)
            )
        }

        runnerTest.setAfterTestHooks(
            getAfterTestHooks(runnerTest, item)
        )

        runnerTest.setAfterEachHooks(
            getAfterEachHooks(runnerTest, afterEachTestBasicHooks)
        )

        return runnerTest
    }

    private fun getBeforeHooks(
        runnerTest: RunnerTest,
        beforeEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerBeforeHooks {
        val beforeHooksContainer = RunnerBeforeHooks(runnerTest, BEFORE_EACH_TEST)

        val beforeHooks = mutableListOf<RunnerHook>()

        // before hooks: basic before-each
        beforeHooks += beforeEachTestBasicHooks

        // before hooks: parent features before-each
        beforeHooks += InheritedHooksFinder.find(
            parentForHooks = beforeHooksContainer,
            phase = BEFORE_EACH_TEST,
            descendingFromRoot = true
        )

        beforeHooksContainer.hooks = beforeHooks
        return beforeHooksContainer
    }

    private fun getAfterTestHooks(
        runnerTest: RunnerTest,
        item: TestWithFilePath
    ): RunnerAfterHooks {
        val afterTestHooksContainer = RunnerAfterHooks(runnerTest, AFTER_TEST)

        val afterTestHooks = mutableListOf<RunnerHook>()

        //  hooks: test after
        for ((index, hookStepCall) in item.test.afterHooks.withIndex()) {
            val runnerComposedHook = RunnerComposedHook(
                parent = afterTestHooksContainer,
                phase = AFTER_TEST,
                source = TestHookSource(testPath = item.test.path)
            )
            runnerComposedHook.setStep(
                RunnerStepNodeFactory.create(
                    parentNode = runnerComposedHook,
                    indexInParent = index,
                    stepCall = hookStepCall
                )
            )
            afterTestHooks += runnerComposedHook
        }

        afterTestHooksContainer.hooks = afterTestHooks
        return afterTestHooksContainer
    }

    private fun getAfterEachHooks(
        runnerTest: RunnerTest,
        afterEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerAfterHooks {
        val afterEachHooksContainer = RunnerAfterHooks(runnerTest, AFTER_EACH_TEST)

        val afterEachHooks = mutableListOf<RunnerHook>()

        // after hooks: parent features after-each
        afterEachHooks += InheritedHooksFinder.find(
            parentForHooks = afterEachHooksContainer,
            phase = AFTER_EACH_TEST,
            descendingFromRoot = false
        )

        // after hooks: basic after-each
        afterEachHooks += afterEachTestBasicHooks

        afterEachHooksContainer.hooks = afterEachHooks
        return afterEachHooksContainer
    }
}
