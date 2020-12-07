package com.testerum.model.util.new_tree_builder

import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path

/**
 * @param R root node
 * @param V virtual container
 */
class PathBasedTreeBuilder<R : ContainerTreeNode, V : ContainerTreeNode>(
    private val nodeFactory: TreeNodeFactory<R, V>,
) {

    private val nodesByPath = HashMap<String, TreeNode>()

    fun createTree(items: List<HasPath>): R {
        // It's very important that Features are sorted to be before Tests.
        // This is accomplished by just sorting by path, because the parent feature path is a prefix of the child test path.
        // The "withoutFileExtension" is important, because otherwise
        //     "backend/expressions/escapeXml 1_1/to escape.test" > "backend/expressions/escapeXml 1_1/to escape XML 1_1.test"
        val sortedItems = items.sortedBy { it.path.withoutFileExtension().toString() }

        for (sortedItem in sortedItems) {
            addItem(sortedItem)
        }

        val rootNode = nodesByPath[""]
            ?: throw IllegalStateException("should not happen: cannot find root node: nodesByPath=$nodesByPath")

        @Suppress("UNCHECKED_CAST") // it's safe because we insuere that we create the root node for the empty path
        return rootNode as R
    }

    private fun addItem(item: HasPath) {
        val parentPath = item.path.getParent()
        val parentNode = getOrCreateParentNode(parentPath)

        val node: TreeNode = nodeFactory.createNode(parentNode, item)
        parentNode.addChild(node)

        nodesByPath[item.path.toString()] = node
    }

    private fun getOrCreateParentNode(path: Path): ContainerTreeNode {
        val existingNode = nodesByPath[path.toString()]
        if (existingNode != null) {
            return existingNode as? ContainerTreeNode
                ?: throw IllegalStateException("cannot add child to node of type [${existingNode.javaClass}] because it's not a container node")
        }

        val newNode = if (path.isEmpty()) {
            nodeFactory.createRootNode()
        } else {
            val parentNode = getOrCreateParentNode(path.getParent())

            val virtualContainer = nodeFactory.createVirtualContainer(parentNode, path)
            parentNode.addChild(virtualContainer)

            virtualContainer
        }
        nodesByPath[path.toString()] = newNode

        return newNode
    }

}
