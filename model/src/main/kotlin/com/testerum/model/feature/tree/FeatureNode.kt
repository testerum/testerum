package com.testerum.model.feature.tree

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.testerum.model.infrastructure.path.Path

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = RootFeatureNode::class   , name = "FEATURE_ROOT"),
    JsonSubTypes.Type(value = FeatureFeatureNode::class, name = "FEATURE_FEATURE"),
    JsonSubTypes.Type(value = TestFeatureNode::class   , name = "FEATURE_TEST")
])
interface FeatureNode {
    val name: String
    val path: Path
    val hasOwnOrDescendantWarnings: Boolean
}
