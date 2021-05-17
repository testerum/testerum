package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.feature.Feature
import com.testerum.model.feature.hooks.HookPhase.AFTER_ALL_TESTS
import com.testerum.model.feature.hooks.HookPhase.BEFORE_ALL_TESTS
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature
import com.testerum.runner_cmdline.runner_tree.nodes.hook.FeatureHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooks
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooks
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook

object RunnerFeatureNodeFactory {

    fun create(
        parentNode: RunnerTreeNode,
        item: Feature
    ): RunnerFeature {
        val feature = RunnerFeature(
            parent = parentNode,
            path = item.path,
            featureName = item.name,
            tags = item.tags,
            feature = item
        )

        feature.setBeforeHooks(
            getBeforeAllHooks(item, feature)
        )
        feature.setAfterHooks(
            getAfterHooks(item, feature)
        )

        return feature
    }

    private fun getBeforeAllHooks(
        item: Feature,
        feature: RunnerFeature
    ): RunnerBeforeHooks {
        val runnerBeforeHooks = RunnerBeforeHooks(parent = feature, hookPhase = BEFORE_ALL_TESTS)

        val beforeAllHooks = mutableListOf<RunnerHook>()
        for (hookStepCall in item.hooks.beforeAll) {
            beforeAllHooks += RunnerComposedHookNodeFactory.create(
                parent = runnerBeforeHooks,
                indexInParent = beforeAllHooks.size,
                stepCall = hookStepCall,
                phase = BEFORE_ALL_TESTS,
                source = FeatureHookSource(item.path)
            )
        }

        runnerBeforeHooks.hooks = beforeAllHooks
        return runnerBeforeHooks
    }

    private fun getAfterHooks(
        item: Feature,
        feature: RunnerFeature
    ): RunnerAfterHooks {
        val runnerAfterHooks = RunnerAfterHooks(parent = feature, hookPhase = AFTER_ALL_TESTS)

        val afterAllHooks = mutableListOf<RunnerHook>()
        for (hookStepCall in item.hooks.afterAll) {
            afterAllHooks += RunnerComposedHookNodeFactory.create(
                parent = runnerAfterHooks,
                indexInParent = afterAllHooks.size,
                stepCall = hookStepCall,
                phase = AFTER_ALL_TESTS,
                source = FeatureHookSource(item.path)
            )
        }

        runnerAfterHooks.hooks = afterAllHooks
        return runnerAfterHooks
    }
}
