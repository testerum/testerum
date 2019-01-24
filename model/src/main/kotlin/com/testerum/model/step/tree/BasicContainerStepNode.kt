package com.testerum.model.step.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class BasicContainerStepNode @JsonCreator constructor(@JsonProperty("path") override val path: Path,
                                                           @JsonProperty("hasOwnOrDescendantWarnings") override val hasOwnOrDescendantWarnings: Boolean = false,
                                                           @JsonProperty("name") val name: String,
                                                           @JsonProperty("children") val children: List<BasicStepNode>) : BasicStepNode
