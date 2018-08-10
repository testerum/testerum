package com.testerum.model.runner.tree

import com.testerum.model.step.StepCall

interface RunnerStepNode : RunnerNode {
    val stepCall: StepCall
}

