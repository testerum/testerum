package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class ManualTest @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                               @JsonProperty("text") val text: String,
                                               @JsonProperty("description") val description: String?,
                                               @JsonProperty("tags") val tags: List<String> = emptyList(),
                                               @JsonProperty("steps") val steps: List<ManualStep> = emptyList()
                                               ) {

    private val _id = path.toString()

    val id: String
        get() = _id

}