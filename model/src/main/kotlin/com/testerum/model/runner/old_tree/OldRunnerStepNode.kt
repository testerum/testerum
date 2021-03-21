package com.testerum.model.runner.old_tree

import com.testerum.model.step.StepCall

interface OldRunnerStepNode : OldRunnerNode {
    val stepCall: StepCall
}

