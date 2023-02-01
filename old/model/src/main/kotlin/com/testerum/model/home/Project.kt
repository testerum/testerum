package com.testerum.model.home

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class Project @JsonCreator constructor(@JsonProperty("name") val name: String,
                                            @JsonProperty("path") val path: String,
                                            @JsonProperty("lastOpened") val lastOpened: LocalDateTime?)
