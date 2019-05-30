package com.testerum.model.runner.config

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.util.escapeFileOrDirectoryName

data class RunConfig @JsonCreator constructor(@JsonProperty("name")           val name: String,
                                              @JsonProperty("path")           val path: Path,
                                              @JsonProperty("oldPath")        val oldPath: Path? = path,
                                              @JsonProperty("settings")       val settings: Map<String, String>,
                                              @JsonProperty("tagsToInclude")  val tagsToInclude: List<String> = emptyList(),
                                              @JsonProperty("tagsToExclude")  val tagsToExclude: List<String> = emptyList(),
                                              @JsonProperty("pathsToInclude") val pathsToInclude: List<Path> = emptyList()) {

    @JsonIgnore
    fun getNewPath(): Path {
        return path.copy(
                fileName = name.escapeFileOrDirectoryName(),
                fileExtension = "json"
        )
    }

}
