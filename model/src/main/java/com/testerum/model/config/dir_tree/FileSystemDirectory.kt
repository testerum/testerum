package com.testerum.model.config.dir_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class FileSystemDirectory @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                                        @JsonProperty("hasChildrenDirectories") val hasChildrenDirectories: Boolean,
                                                        @JsonProperty("childrenDirectories") val childrenDirectories: List<FileSystemDirectory> = emptyList())
