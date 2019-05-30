package com.testerum.model.runner.config

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FileRunConfig @JsonCreator constructor(@JsonProperty("name")          val name: String,
                                                  @JsonProperty("settings")      val settings: Map<String, String>,
                                                  @JsonProperty("tagsToInclude") val tagsToInclude: List<String> = emptyList(),
                                                  @JsonProperty("tagsToExclude") val tagsToExclude: List<String> = emptyList(),
                                                  @JsonProperty("paths")         val paths: List<String> = emptyList())
