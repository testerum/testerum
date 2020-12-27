package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.feature.Feature
import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature
import com.testerum.runner_cmdline.runner_tree.nodes.hook.FeatureHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerAfterHooksList
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBeforeHooksList
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerHook

object RunnerFeatureNodeFactory {

    fun create(
        parentNode: ContainerTreeNode,
        item: Feature
    ): RunnerFeature {
        val feature = RunnerFeature(
            parent = parentNode,
            indexInParent = parentNode.childrenCount,
            featurePathFromRoot = item.path.directories,
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
    ): RunnerBeforeHooksList {
        val beforeAllHooks = mutableListOf<RunnerHook>()
        for (hookStepCall in item.hooks.beforeAll) {
            beforeAllHooks += RunnerComposedHookNodeFactory.create(
                parent = feature,
                indexInParent = beforeAllHooks.size,
                stepCall = hookStepCall,
                phase = HookPhase.BEFORE_ALL_TESTS,
                source = FeatureHookSource(item.path)
            )
        }

        return RunnerBeforeHooksList(beforeAllHooks)
    }

    private fun getAfterHooks(
        item: Feature,
        feature: RunnerFeature
    ): RunnerAfterHooksList {
        val afterAllHooks = mutableListOf<RunnerHook>()
        for (hookStepCall in item.hooks.afterAll) {
            afterAllHooks += RunnerComposedHookNodeFactory.create(
                parent = feature,
                indexInParent = afterAllHooks.size,
                stepCall = hookStepCall,
                phase = HookPhase.AFTER_ALL_TESTS,
                source = FeatureHookSource(item.path)
            )
        }

        return RunnerAfterHooksList(afterAllHooks)
    }
}
