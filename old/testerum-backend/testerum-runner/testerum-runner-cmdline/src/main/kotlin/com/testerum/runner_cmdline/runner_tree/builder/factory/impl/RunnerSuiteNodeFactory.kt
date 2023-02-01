package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.feature.Feature
import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooks
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBasicHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooks
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.SuiteHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite

object RunnerSuiteNodeFactory {

    fun create(
        item: HasPath?,
        executionName: String?,
        glueClassNames: List<String>,
        beforeAllTestsBasicHooks: List<RunnerBasicHook>,
        afterAllTestsBasicHooks: List<RunnerBasicHook>
    ): RunnerSuite {
        val rootFeature = item as? Feature
            ?: throw IllegalStateException(
                "INTERNAL ERROR: root item is either null or not a ${Feature::class.simpleName}" +
                    ": itemClass=[${item?.javaClass}]" +
                    ", item=[$item]"
            )

        val runnerSuite = RunnerSuite(executionName, glueClassNames, rootFeature)

        runnerSuite.setBeforeHooks(
            getBeforeHooks(runnerSuite, rootFeature, beforeAllTestsBasicHooks)
        )
        runnerSuite.setAfterHooks(
            getAfterHooks(runnerSuite, rootFeature, afterAllTestsBasicHooks)
        )

        return runnerSuite
    }

    private fun getBeforeHooks(
        runnerSuite: RunnerSuite,
        rootFeature: Feature,
        beforeAllTestsBasicHooks: List<RunnerBasicHook>
    ): RunnerBeforeHooks {
        val beforeAllHooks = RunnerBeforeHooks(runnerSuite, HookPhase.BEFORE_ALL_TESTS)

        val beforeHooks = mutableListOf<RunnerHook>()

        // hooks: basic before-all
        beforeHooks += beforeAllTestsBasicHooks

        // hooks: composed before-all
        for ((index, hookStepCall) in rootFeature.hooks.beforeAll.withIndex()) {
            beforeHooks += RunnerComposedHookNodeFactory.create(
                parent = beforeAllHooks,
                indexInParent = index,
                stepCall = hookStepCall,
                phase = HookPhase.BEFORE_ALL_TESTS,
                source = SuiteHookSource
            )
        }

        beforeAllHooks.hooks = beforeHooks
        return beforeAllHooks
    }

    private fun getAfterHooks(
        runnerSuite: RunnerSuite,
        rootFeature: Feature,
        afterAllTestsBasicHooks: List<RunnerBasicHook>
    ): RunnerAfterHooks {
        val runnerAfterHooks = RunnerAfterHooks(runnerSuite, HookPhase.AFTER_ALL_TESTS)

        // hooks: composed after-all
        val afterHooks = mutableListOf<RunnerHook>()
        for (hookStepCall in rootFeature.hooks.afterAll) {
            afterHooks += RunnerComposedHookNodeFactory.create(
                parent = runnerAfterHooks,
                indexInParent = afterHooks.size,
                stepCall = hookStepCall,
                phase = HookPhase.AFTER_ALL_TESTS,
                source = SuiteHookSource
            )
        }

        // hooks: basic after-all
        afterHooks += afterAllTestsBasicHooks

        runnerAfterHooks.hooks = afterHooks
        return runnerAfterHooks
    }
}
