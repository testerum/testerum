package com.testerum.model.infrastructure.path

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.nio.file.Path as JavaPath

data class PathInfo @JsonCreator constructor(
        @JsonProperty("pathAsString") val pathAsString: String,
        @JsonProperty("isValidPath") val isValidPath: Boolean,
        @JsonProperty("isExistingPath") val isExistingPath: Boolean,
        @JsonProperty("isProjectDirectory") val isProjectDirectory: Boolean,
        @JsonProperty("canCreateChild") val canCreateChild: Boolean)
