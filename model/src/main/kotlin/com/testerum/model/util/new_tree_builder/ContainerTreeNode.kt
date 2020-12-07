package com.testerum.model.util.new_tree_builder

interface ContainerTreeNode : TreeNode {

    val childrenCount: Int

    fun addChild(child: TreeNode)

}
