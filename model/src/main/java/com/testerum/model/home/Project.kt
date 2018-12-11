package com.testerum.model.home

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class Project  @JsonCreator constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("path") val path: Path)