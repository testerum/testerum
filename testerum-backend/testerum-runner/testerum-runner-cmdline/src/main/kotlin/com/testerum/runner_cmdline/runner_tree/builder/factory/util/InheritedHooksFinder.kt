package com.testerum.runner_cmdline.runner_tree.builder.factory.util

import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner_cmdline.runner_tree.builder.factory.impl.RunnerComposedHookNodeFactory
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature
import com.testerum.runner_cmdline.runner_tree.nodes.hook.FeatureHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerComposedHook
import com.testerum.runner_cmdline.runner_tree.nodes.hook.SuiteHookSource
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite

object InheritedHooksFinder {

    fun <P> find(
        parentForHooks: P,
        phase: HookPhase,
        descendingFromRoot: Boolean,
    ): List<RunnerComposedHook> where P : TreeNode, P: RunnerTreeNode {
        val featuresOrSuite = mutableListOf<RunnerTreeNode>()

        var currentNode: RunnerTreeNode? = parentForHooks
        while (currentNode != null) {
            if (currentNode is RunnerFeature || currentNode is RunnerSuite) {
                featuresOrSuite += currentNode
            }

            currentNode = currentNode.parent
        }

        // the list if sorted from leaf to root; reverse if needed
        if (descendingFromRoot) {
            featuresOrSuite.reverse()
        }

        val hooks = mutableListOf<RunnerComposedHook>()

        for (featureOrSuite in featuresOrSuite) {
            when (featureOrSuite) {
                is RunnerFeature -> {
                    for (hookStepCall in featureOrSuite.feature.hooks.getByPhase(phase)) {
                        hooks += RunnerComposedHookNodeFactory.create(
                            parent = parentForHooks,
                            indexInParent = hooks.size,
                            stepCall = hookStepCall,
                            phase = phase,
                            source = FeatureHookSource(featurePath = featureOrSuite.feature.path)
                        )
                    }
                }
                is RunnerSuite -> {
                    for (hookStepCall in featureOrSuite.feature.hooks.getByPhase(phase)) {
                        hooks += RunnerComposedHookNodeFactory.create(
                            parent = parentForHooks,
                            indexInParent = hooks.size,
                            stepCall = hookStepCall,
                            phase = phase,
                            source = SuiteHookSource
                        )
                    }
                }
            }
        }

        return hooks
    }
}
