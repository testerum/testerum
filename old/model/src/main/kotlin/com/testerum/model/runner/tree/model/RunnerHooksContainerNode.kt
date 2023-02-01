package com.testerum.model.runner.tree.model

import com.testerum.model.step.StepCall

interface RunnerHooksContainerNode: RunnerNode {
    val beforeAllHooks: RunnerHooksNode
    val beforeEachStepCalls: List<StepCall>
    val afterEachStepCalls: List<StepCall>
    val afterAllHooks: RunnerHooksNode
}
