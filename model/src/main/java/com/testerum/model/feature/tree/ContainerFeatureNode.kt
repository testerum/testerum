package com.testerum.model.feature.tree

interface ContainerFeatureNode : FeatureNode {
    val children: List<FeatureNode>
}