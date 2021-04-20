package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.feature.hooks.HookPhase.AFTER_EACH_TEST
import com.testerum.model.feature.hooks.HookPhase.AFTER_TEST
import com.testerum.model.feature.hooks.HookPhase.BEFORE_EACH_TEST
import com.testerum.model.test.TestModel
import com.testerum.model.test.scenario.Scenario
import com.testerum.runner_cmdline.runner_tree.builder.factory.util.InheritedHooksFinder
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooks
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBasicHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooks
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerComposedHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.TestHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test.RunnerScenario
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import java.nio.file.Path as JavaPath

object RunnerScenarioNodeFactory {

    fun create(
        parentNode: RunnerTreeNode,
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
        runnerScenario.setAfterTestHooks(
            getAfterTestHooks(runnerScenario, test)
        )

        runnerScenario.setAfterEachHooks(
            getAfterEachHooks(runnerScenario, afterEachTestBasicHooks)
        )

        return runnerScenario
    }

    private fun getBeforeHooks(
        runnerScenario: RunnerScenario,
        beforeEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerBeforeHooks {
        val runnerBeforeHooks = RunnerBeforeHooks(parent = runnerScenario, hookPhase = BEFORE_EACH_TEST)

        val beforeHooks = mutableListOf<RunnerHook>()

        // before hooks: basic before-each
        beforeHooks += beforeEachTestBasicHooks

        // before hooks: parent features before-each
        beforeHooks += InheritedHooksFinder.find(
            parentForHooks = runnerBeforeHooks,
            phase = HookPhase.BEFORE_EACH_TEST,
            descendingFromRoot = true
        )

        runnerBeforeHooks.hooks = beforeHooks
        return runnerBeforeHooks
    }

    private fun getSteps(
        runnerScenario: RunnerScenario,
        test: TestModel
    ): MutableList<RunnerStep> {
        val runnerSteps = mutableListOf<RunnerStep>()

        for ((stepIndexInParent, stepCall) in test.stepCalls.withIndex()) {
            runnerSteps += RunnerStepNodeFactory.create(runnerScenario, stepIndexInParent, stepCall)
        }

        return runnerSteps
    }

    private fun getAfterTestHooks(
        runnerScenario: RunnerScenario,
        test: TestModel
    ): RunnerAfterHooks {
        val runnerAfterScenarioHooksContainer = RunnerAfterHooks(runnerScenario, AFTER_TEST)

        val afterHooks = mutableListOf<RunnerHook>()
        //  hooks: test after
        for ((index, hookStepCall) in test.afterHooks.withIndex()) {
            val runnerComposedHook = RunnerComposedHook(
                parent = runnerAfterScenarioHooksContainer,
                phase = AFTER_TEST,
                source = TestHookSource(testPath = test.path)
            )
            runnerComposedHook.setStep(
                RunnerStepNodeFactory.create(
                    parentNode = runnerComposedHook,
                    indexInParent = index,
                    stepCall = hookStepCall
                )
            )
            afterHooks += runnerComposedHook
        }

        runnerAfterScenarioHooksContainer.hooks = afterHooks
        return runnerAfterScenarioHooksContainer
    }

    private fun getAfterEachHooks(
        runnerScenario: RunnerScenario,
        afterEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerAfterHooks {
        val runnerAfterEachHooksContainer = RunnerAfterHooks(runnerScenario, AFTER_EACH_TEST)

        val afterHooks = mutableListOf<RunnerHook>()

        // after hooks: parent features after-each
        afterHooks += InheritedHooksFinder.find(
            parentForHooks = runnerAfterEachHooksContainer,
            phase = AFTER_EACH_TEST,
            descendingFromRoot = false
        )

        // after hooks: basic after-each
        afterHooks += afterEachTestBasicHooks

        runnerAfterEachHooksContainer.hooks = afterHooks
        return runnerAfterEachHooksContainer
    }
}
