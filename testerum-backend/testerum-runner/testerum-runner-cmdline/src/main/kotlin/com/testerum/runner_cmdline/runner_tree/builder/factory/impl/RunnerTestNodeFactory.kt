package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.runner_cmdline.runner_tree.builder.TestWithFilePath
import com.testerum.runner_cmdline.runner_tree.builder.factory.util.InheritedHooksFinder
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooksList
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBasicHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooksList
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
                RunnerStepNodeFactory.create(runnerTest, stepIndexInParent, stepCall, logEvents = true)
            )
        }

        runnerTest.setAfterHooks(
            getAfterHooks(runnerTest, item, afterEachTestBasicHooks)
        )

        return runnerTest
    }

    private fun getBeforeHooks(
        runnerTest: RunnerTest,
        beforeEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerBeforeHooksList {
        val beforeHooks = mutableListOf<RunnerHook>()

        // before hooks: basic before-each
        beforeHooks += beforeEachTestBasicHooks

        // before hooks: parent features before-each
        beforeHooks += InheritedHooksFinder.find(
            parentForHooks = runnerTest,
            phase = HookPhase.BEFORE_EACH_TEST,
            descendingFromRoot = true
        )

        return RunnerBeforeHooksList(beforeHooks)
    }

    private fun getAfterHooks(
        runnerTest: RunnerTest,
        item: TestWithFilePath,
        afterEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerAfterHooksList {
        val afterHooks = mutableListOf<RunnerHook>()

        //  hooks: test after
        for (hookStepCall in item.test.afterHooks) {
            val runnerComposedHook = RunnerComposedHook(
                parent = runnerTest,
                indexInParent = afterHooks.size,
                phase = HookPhase.AFTER_EACH_TEST,
                source = TestHookSource(testPath = item.test.path)
            )
            runnerComposedHook.setStep(
                RunnerStepNodeFactory.create(
                    parentNode = runnerComposedHook,
                    indexInParent = 0,
                    stepCall = hookStepCall,
                    logEvents = false
                )
            )
            afterHooks += runnerComposedHook
        }

        // after hooks: parent features after-each
        afterHooks += InheritedHooksFinder.find(
            parentForHooks = runnerTest,
            phase = HookPhase.AFTER_EACH_TEST,
            descendingFromRoot = false
        )

        // after hooks: basic after-each
        afterHooks += afterEachTestBasicHooks

        return RunnerAfterHooksList(afterHooks)
    }
}
