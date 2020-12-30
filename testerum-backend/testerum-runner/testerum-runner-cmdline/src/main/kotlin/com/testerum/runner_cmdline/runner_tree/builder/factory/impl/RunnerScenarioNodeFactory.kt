package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.test.TestModel
import com.testerum.model.test.scenario.Scenario
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner_cmdline.runner_tree.builder.factory.util.InheritedHooksFinder
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooksList
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBasicHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooksList
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerComposedHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.TestHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test.RunnerScenario
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import java.nio.file.Path as JavaPath

object RunnerScenarioNodeFactory {

    fun create(
        parentNode: TreeNode,
        test: TestModel,
        filePath: JavaPath,
        scenarioWithOriginalIndex: Pair<Int, Scenario>,
        filteredScenarioIndex: Int,
        beforeEachTestBasicHooks: List<RunnerBasicHook>,
        afterEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerScenario {
        val originalScenarioIndex = scenarioWithOriginalIndex.first
        val scenario = scenarioWithOriginalIndex.second

        val runnerScenario = RunnerScenario(
            parent = parentNode,
            test = test,
            scenario = scenario,
            originalScenarioIndex = originalScenarioIndex,
            filteredScenarioIndex = filteredScenarioIndex,
            filePath = filePath,
        )

        runnerScenario.setBeforeHooks(
            getBeforeHooks(runnerScenario, beforeEachTestBasicHooks)
        )
        runnerScenario.setChildren(
            getSteps(runnerScenario, test)
        )
        runnerScenario.setAfterHooks(
            getAfterHooks(runnerScenario, test, afterEachTestBasicHooks)
        )

        return runnerScenario
    }

    private fun getBeforeHooks(
        runnerScenario: RunnerScenario,
        beforeEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerBeforeHooksList {
        val beforeHooks = mutableListOf<RunnerHook>()

        // before hooks: basic before-each
        beforeHooks += beforeEachTestBasicHooks

        // before hooks: parent features before-each
        beforeHooks += InheritedHooksFinder.find(
            parentForHooks = runnerScenario,
            phase = HookPhase.BEFORE_EACH_TEST,
            descendingFromRoot = true
        )

        return RunnerBeforeHooksList(beforeHooks)
    }

    private fun getSteps(
        runnerScenario: RunnerScenario,
        test: TestModel
    ): MutableList<RunnerStep> {
        val runnerSteps = mutableListOf<RunnerStep>()

        for ((stepIndexInParent, stepCall) in test.stepCalls.withIndex()) {
            runnerSteps += RunnerStepNodeFactory.create(runnerScenario, stepIndexInParent, stepCall, logEvents = true)
        }

        return runnerSteps
    }

    private fun getAfterHooks(
        runnerScenario: RunnerScenario,
        test: TestModel,
        afterEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerAfterHooksList {
        val afterHooks = mutableListOf<RunnerHook>()

        //  hooks: test after
        for (hookStepCall in test.afterHooks) {
            val runnerComposedHook = RunnerComposedHook(
                parent = runnerScenario,
                indexInParent = afterHooks.size,
                phase = HookPhase.AFTER_EACH_TEST,
                source = TestHookSource(testPath = test.path)
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
            parentForHooks = runnerScenario,
            phase = HookPhase.AFTER_EACH_TEST,
            descendingFromRoot = false
        )

        // after hooks: basic after-each
        afterHooks += afterEachTestBasicHooks

        return RunnerAfterHooksList(afterHooks)
    }
}
