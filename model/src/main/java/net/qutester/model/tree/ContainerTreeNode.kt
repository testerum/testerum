package net.qutester.model.tree

interface ContainerTreeNode : TreeNode {
    val children: MutableList<TreeNode>;
}