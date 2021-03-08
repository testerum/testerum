package com.testerum.model.runner.old_tree

import com.testerum.model.step.StepCall

interface RunnerStepNode : RunnerNode {
    val stepCall: StepCall
}

