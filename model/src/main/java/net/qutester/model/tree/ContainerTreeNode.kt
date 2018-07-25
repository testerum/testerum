package net.qutester.model.tree

interface ContainerTreeNode : TreeNode {
    val children: List<TreeNode>
}