package com.testerum.model.main_tree

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.testerum.model.infrastructure.path.Path

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = RootMainNode::class   , name = "MAIN_ROOT"),
    JsonSubTypes.Type(value = FeatureMainNode::class, name = "MAIN_FEATURE"),
    JsonSubTypes.Type(value = TestMainNode::class   , name = "MAIN_TEST")
])
interface MainNode {
    val name: String
    val path: Path
    val hasOwnOrDescendantWarnings: Boolean
}