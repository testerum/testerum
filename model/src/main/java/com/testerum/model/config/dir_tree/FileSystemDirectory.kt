package com.testerum.model.config.dir_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FileSystemDirectory @JsonCreator constructor(@JsonProperty("name") val name: String,
                                                        @JsonProperty("absoluteJavaPath") val absoluteJavaPath: String,
                                                        @JsonProperty("hasChildrenDirectories") val hasChildrenDirectories: Boolean,
                                                        @JsonProperty("childrenDirectories") val childrenDirectories: List<FileSystemDirectory> = emptyList())
