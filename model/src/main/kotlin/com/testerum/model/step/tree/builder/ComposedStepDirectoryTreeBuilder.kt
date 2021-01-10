package com.testerum.model.step.tree.builder

import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.PathBasedTreeBuilder
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.model.util.new_tree_builder.TreeNodeFactory

class ComposedStepDirectoryTreeBuilder {
    private val items = mutableListOf<PathItem>()

    fun addComposedStepDirectory(relativeDirectoryPathParts: List<String>) {
        items += PathItem(Path(
            directories = relativeDirectoryPathParts,
            fileName = null,
            fileExtension = null
        ))
    }

    fun createTree(): ComposedContainerStepNode {
        val composedStepsDirTreeBuilder = PathBasedTreeBuilder(
            ComposedStepDirectoryTreeNodeFactory()
        )
        return composedStepsDirTreeBuilder.createTree(items)
    }
}

private class PathItem(override val path: Path) : HasPath

private class ComposedStepDirectoryTreeNodeFactory: TreeNodeFactory<ComposedContainerStepNode, ComposedContainerStepNode> {
    override fun createRootNode(item: HasPath?): ComposedContainerStepNode {
        return ComposedContainerStepNode(
            path = Path.EMPTY,
            name = "Composed Steps"
        )
    }

    override fun createVirtualContainer(parentNode: ContainerTreeNode, path: Path): ComposedContainerStepNode {
        return ComposedContainerStepNode(
            path = path,
            name = path.directories.last()
        )
    }

    override fun createNode(parentNode: ContainerTreeNode, item: HasPath): TreeNode {
        val itemAsPathItem = item as? PathItem
            ?: throw IllegalArgumentException("attempted to add child node of unexpected type [${item.javaClass}]: [$item]")

        return ComposedContainerStepNode(
            path = itemAsPathItem.path,
            name = itemAsPathItem.path.directories.last()
        )
    }
}
