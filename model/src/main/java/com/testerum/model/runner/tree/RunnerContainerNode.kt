package com.testerum.model.runner.tree

import com.testerum.model.runner.tree.RunnerNode

interface RunnerContainerNode : RunnerNode {
    val children: List<RunnerNode>
}