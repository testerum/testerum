package com.testerum.model.home

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Home @JsonCreator constructor(
        @JsonProperty("quote") val quote: Quote,
        @JsonProperty("testerumVersion") val testerumVersion: String,
        @JsonProperty("recentProjects") val recentProjects: List<Project>)
