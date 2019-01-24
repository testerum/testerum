package com.testerum.model.config.dir_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FileSystemDirectory @JsonCreator constructor(@JsonProperty("name") val name: String,
                                                        @JsonProperty("absoluteJavaPath") val absoluteJavaPath: String,
                                                        @JsonProperty("isProject") val isProject: Boolean,
                                                        @JsonProperty("canCreateChild") val canCreateChild: Boolean,
                                                        @JsonProperty("hasChildrenDirectories") val hasChildrenDirectories: Boolean, // this flag is needed because (for performance reasons), we don't return the entire tree, so "childrenDirectories" may be empty, even though the directory has children
                                                        @JsonProperty("childrenDirectories") val childrenDirectories: List<FileSystemDirectory> = emptyList())
