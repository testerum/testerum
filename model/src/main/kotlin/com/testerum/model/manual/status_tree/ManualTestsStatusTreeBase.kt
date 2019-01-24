package com.testerum.model.manual.status_tree

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.testerum.model.feature.tree.FeatureFeatureNode
import com.testerum.model.feature.tree.RootFeatureNode
import com.testerum.model.feature.tree.TestFeatureNode
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.enums.ManualTestStatus

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = ManualTestsStatusTreeRoot::class   , name = "ROOT"),
    JsonSubTypes.Type(value = ManualTestsStatusTreeContainer::class, name = "CONTAINER"),
    JsonSubTypes.Type(value = ManualTestsStatusTreeNode::class   , name = "NODE")
])
interface ManualTestsStatusTreeBase {
    val path: Path
    val name: String
    val status: ManualTestStatus
}
