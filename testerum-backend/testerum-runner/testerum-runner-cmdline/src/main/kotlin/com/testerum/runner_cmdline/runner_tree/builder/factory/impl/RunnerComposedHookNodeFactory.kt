package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.step.StepCall
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.HookSource
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerComposedHook

object RunnerComposedHookNodeFactory {

    fun create(
        parent: TreeNode,
        indexInParent: Int,
        stepCall: StepCall,
        phase: HookPhase,
        source: HookSource,
    ): RunnerComposedHook {
        val runnerComposedHook = RunnerComposedHook(
            parent = parent,
            indexInParent = indexInParent,
            phase = phase,
            source = source
        )

        runnerComposedHook.setStep(
            RunnerStepNodeFactory.create(
                parentNode = runnerComposedHook,
                indexInParent = 0,
                stepCall = stepCall,
                logEvents = false
            )
        )

        return runnerComposedHook
    }
}
