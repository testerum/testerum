package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.step.StepCall
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.HookSource
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerComposedHook

object RunnerComposedHookNodeFactory {

    fun create(
        parent: RunnerTreeNode,
        indexInParent: Int,
        stepCall: StepCall,
        phase: HookPhase,
        source: HookSource,
    ): RunnerComposedHook {
        val runnerComposedHook = RunnerComposedHook(
            parent = parent,
            phase = phase,
            source = source
        )

        runnerComposedHook.setStep(
            RunnerStepNodeFactory.create(
                parentNode = runnerComposedHook,
                indexInParent = indexInParent,
                stepCall = stepCall
            )
        )

        return runnerComposedHook
    }
}
