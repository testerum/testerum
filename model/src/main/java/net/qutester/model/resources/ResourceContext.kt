package net.qutester.model.resources

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path

data class ResourceContext @JsonCreator constructor(
        /** Full path to the file (path + name + extension). Acts as ID*/
        @JsonProperty("path") val path: Path,
        @JsonProperty("oldPath") val oldPath: Path? = null,
        @JsonProperty("body") val body: String
)
