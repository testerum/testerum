package net.qutester.model.infrastructure.path

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RenamePath @JsonCreator constructor (
        @JsonProperty("path") val path: Path,
        @JsonProperty("newName") val newName: String
)