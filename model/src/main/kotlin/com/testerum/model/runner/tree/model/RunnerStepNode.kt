package com.testerum.model.runner.tree.model

import com.testerum.model.step.StepCall

interface RunnerStepNode : RunnerNode {
    val stepCall: StepCall
}

