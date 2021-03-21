package com.testerum.model.util.new_tree_builder

import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import java.lang.Integer.min

/**
 * @param R root node
 * @param V virtual container
 */
class PathBasedTreeBuilder<R : ContainerTreeNode, V : ContainerTreeNode>(
    private val nodeFactory: TreeNodeFactory<R, V>,
) {

    companion object {
        val NODES_COMPARATOR = Comparator<HasPath> { left, right ->
            val leftDirs = left.path.directories
            val rightDirs = right.path.directories
            val directoryComparisonResult = compareCommonDirParts(leftDirs, rightDirs)
            if (directoryComparisonResult != 0) {
                return@Comparator directoryComparisonResult
            }

            val leftIsDirectory = left.path.isDirectory()
            val rightIsDirectory = right.path.isDirectory()

            if (leftDirs.size < rightDirs.size) {
                if (leftIsDirectory && rightIsDirectory) { return@Comparator -1 }
                if (leftIsDirectory && !rightIsDirectory) { return@Comparator -1 }
                if (!leftIsDirectory && rightIsDirectory) { return@Comparator 1 }
                if (!leftIsDirectory && !rightIsDirectory) { return@Comparator 1 }
            }
            if (leftDirs.size > rightDirs.size) {
                if (leftIsDirectory && rightIsDirectory) { return@Comparator 1 }
                if (leftIsDirectory && !rightIsDirectory) { return@Comparator -1 }
                if (!leftIsDirectory && rightIsDirectory) { return@Comparator 1 }
                if (!leftIsDirectory && !rightIsDirectory) { return@Comparator -1 }
            }

            val leftFile = left.path.fileName ?: ""
            val rightFile = right.path.fileName ?: ""

            val fileNameComparisonResult = leftFile.compareTo(rightFile, true)
            if (fileNameComparisonResult != 0) {
                return@Comparator fileNameComparisonResult
            }

            val leftFileExtension = left.path.fileExtension ?: ""
            val rightFileExtension = right.path.fileExtension ?: ""

            return@Comparator leftFileExtension.compareTo(rightFileExtension, true)
        }

        private fun compareCommonDirParts(leftDirs: List<String>, rightDirs: List<String>): Int {
            val minDirSize = min(leftDirs.size, rightDirs.size)

            for (index in 0 until minDirSize) {
                val leftDir = leftDirs[index]
                val rightDir = rightDirs[index]

                val dirNameComparisonResult = leftDir.compareTo(rightDir, true)
                if (dirNameComparisonResult != 0) {
                    return dirNameComparisonResult
                }
            }

            return 0
        }
    }

    private val nodesByPath = HashMap<String, TreeNode>()

    fun createTree(items: List<HasPath>): R {
        // It's very important that Features are sorted to be before Tests, otherwise it will create a virtual container instead of the real one.
        // This is accomplished by just sorting by path, because the parent feature path is a prefix of the child test path.
        // The "withoutFileExtension" is important, because otherwise
        //     "backend/expressions/escapeXml 1_1/to escape.test" > "backend/expressions/escapeXml 1_1/to escape XML 1_1.test"
        val sortedItems = items.sortedWith(NODES_COMPARATOR)

        for (sortedItem in sortedItems) {
            addItem(sortedItem)
        }

        val rootNode = getOrCreateParentNode(Path.EMPTY)

        @Suppress("UNCHECKED_CAST") // it's safe because we insure that we create the root node for the empty path
        return rootNode as R
    }

    private fun addItem(item: HasPath) {
        val path = item.path
        if (path.isEmpty()) {
            // this is the root
            val root = nodeFactory.createRootNode(item)
            nodesByPath[path.toString()] = root
        } else {
            // non-root
            val parentPath = path.getParent()
            val parentNode = getOrCreateParentNode(parentPath)

            val node: TreeNode = nodeFactory.createNode(parentNode, item)
            parentNode.addChild(node)

            nodesByPath[item.path.toString()] = node
        }
    }

    private fun getOrCreateParentNode(path: Path): ContainerTreeNode {
        val existingNode = nodesByPath[path.toString()]
        if (existingNode != null) {
            return existingNode as? ContainerTreeNode
                ?: throw IllegalStateException("cannot add child to node of type [${existingNode.javaClass}] because it's not a container node")
        }

        val newNode = if (path.isEmpty()) {
            nodeFactory.createRootNode(null)
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
