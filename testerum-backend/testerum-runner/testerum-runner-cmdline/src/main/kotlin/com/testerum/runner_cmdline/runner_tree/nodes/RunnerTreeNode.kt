package com.testerum.runner_cmdline.runner_tree.nodes

abstract class RunnerTreeNode {

    abstract val id: String
    abstract val parent: RunnerTreeNode?

    val eventKey: String
        get() {
            return id
        }

    abstract fun addToString(destination: StringBuilder, indentLevel: Int)
}
