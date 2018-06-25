package net.qutester.model.config.dir_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path

data class FileSystemDirectory @JsonCreator constructor(
        @JsonProperty("path") val path: Path,
        @JsonProperty("hasChildrenDirectories") val hasChildrenDirectories: Boolean,
        @JsonProperty("childrenDirectories") val childrenDirectories: List<FileSystemDirectory> = emptyList()
)
