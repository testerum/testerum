package com.testerum.model.feature.tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestProperties

data class TestFeatureNode @JsonCreator constructor(@JsonProperty("name") override val name: String,
                                                    @JsonProperty("path") override val path: Path,
                                                    @JsonProperty("properties") val properties: TestProperties,
                                                    @JsonProperty("hasOwnOrDescendantWarnings") override val hasOwnOrDescendantWarnings: Boolean = false): FeatureNode
