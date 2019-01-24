package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class ManualTreeTest @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                                   @JsonProperty("testName") val testName: String)
