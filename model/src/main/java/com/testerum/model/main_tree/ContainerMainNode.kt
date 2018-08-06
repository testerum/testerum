package com.testerum.model.main_tree

interface ContainerMainNode : MainNode {
    val children: List<MainNode>
}