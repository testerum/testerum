package com.testerum.model.util.new_tree_builder

import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path

/**
 * @param R root node
 * @param V virtual container
 */
interface TreeNodeFactory<R : ContainerTreeNode, V : ContainerTreeNode> {

    fun createRootNode(item: HasPath?): R

    fun createVirtualContainer(parentNode: ContainerTreeNode, path: Path): V

    fun createNode(parentNode: ContainerTreeNode, item: HasPath): TreeNode

}
