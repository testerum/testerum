package com.testerum.runner.runner_tree.nodes

import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner.events.model.position.PositionInParent

abstract class RunnerTreeNode {

    abstract val parent: RunnerTreeNode?

    abstract val positionInParent: PositionInParent

    val eventKey: EventKey
        get() {
            val positions = mutableListOf<PositionInParent>()

            var currentNode: RunnerTreeNode? = this
            while (currentNode != null) {
                positions.add(currentNode.positionInParent)

                currentNode = currentNode.parent
            }

            positions.reverse()

            return EventKey(positions)
        }

}