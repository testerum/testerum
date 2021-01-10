package com.testerum.model.step.tree.builder

import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.model.step.tree.ComposedStepStepNode
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.PathBasedTreeBuilder
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.model.util.new_tree_builder.TreeNodeFactory

class ComposedStepTreeBuilder {

    fun createTree(items: List<ComposedStepDef>): ComposedContainerStepNode {
        val composedStepsTreeBuilder = PathBasedTreeBuilder(
            ComposedStepsTreeNodeFactory()
        )
        return composedStepsTreeBuilder.createTree(items)
    }
}

private class ComposedStepsTreeNodeFactory : TreeNodeFactory<ComposedContainerStepNode, ComposedContainerStepNode> {
    override fun createRootNode(item: HasPath?): ComposedContainerStepNode {
        return ComposedContainerStepNode(
            path = Path.EMPTY,
            name = "Basic Steps"
        )
    }

    override fun createVirtualContainer(parentNode: ContainerTreeNode, path: Path): ComposedContainerStepNode {
        return ComposedContainerStepNode(
            path = path,
            name = path.directories.last()
        )
    }

    override fun createNode(parentNode: ContainerTreeNode, item: HasPath): TreeNode {
        val itemAsComposedStepDef = item as? ComposedStepDef
            ?: throw IllegalArgumentException("attempted to add child node of unexpected type [${item.javaClass}]: [$item]")

        return ComposedStepStepNode(
            path = itemAsComposedStepDef.path,
            hasOwnOrDescendantWarnings = itemAsComposedStepDef.hasOwnOrDescendantWarnings,
            stepDef = itemAsComposedStepDef
        )
    }
}

