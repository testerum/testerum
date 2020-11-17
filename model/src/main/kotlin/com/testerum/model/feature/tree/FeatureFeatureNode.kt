package com.testerum.model.feature.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class FeatureFeatureNode @JsonCreator constructor(@JsonProperty("name") override val name: String,
                                                       @JsonProperty("path") override val path: Path,
                                                       @JsonProperty("children") override val children: List<FeatureNode>,
                                                       @JsonProperty("hasOwnOrDescendantWarnings") override val hasOwnOrDescendantWarnings: Boolean = false,
                                                       @JsonProperty("hasHooks") val hasHooks: Boolean
): ContainerFeatureNode
