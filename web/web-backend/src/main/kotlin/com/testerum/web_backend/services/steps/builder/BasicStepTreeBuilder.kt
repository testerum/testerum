package com.testerum.web_backend.services.steps.builder

import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.tree.BasicContainerStepNode
import com.testerum.model.step.tree.BasicStepStepNode
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.PathBasedTreeBuilder
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.model.util.new_tree_builder.TreeNodeFactory

class BasicStepTreeBuilder {



    fun createTree(items: List<BasicStepDef>): BasicContainerStepNode {
        val basicStepsTreeBuilder = PathBasedTreeBuilder(
            BasicStepTreeNodeFactory()
        )
        return basicStepsTreeBuilder.createTree(items)
    }
}

private class BasicStepTreeNodeFactory: TreeNodeFactory<BasicContainerStepNode, BasicContainerStepNode> {
    override fun createRootNode(item: HasPath?): BasicContainerStepNode {
        return BasicContainerStepNode(
            path = Path.EMPTY,
            name = "Basic Steps"
        )
    }

    override fun createVirtualContainer(parentNode: ContainerTreeNode, path: Path): BasicContainerStepNode {
        return BasicContainerStepNode(
            path = path,
            name = path.directories.last()
        )
    }

    override fun createNode(parentNode: ContainerTreeNode, item: HasPath): TreeNode {
        val itemAsBasicStepDef = item as? BasicStepDef
            ?: throw IllegalArgumentException("attempted to add child node of unexpected type [${item.javaClass}]: [$item]")

        return BasicStepStepNode(
            path = itemAsBasicStepDef.path,
            hasOwnOrDescendantWarnings = itemAsBasicStepDef.hasOwnOrDescendantWarnings,
            stepDef = itemAsBasicStepDef
        )
    }
}
