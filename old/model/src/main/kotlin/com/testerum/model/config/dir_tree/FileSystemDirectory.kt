package com.testerum.model.config.dir_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FileSystemDirectory @JsonCreator constructor(@JsonProperty("name") override val name: String,
                                                        @JsonProperty("absoluteJavaPath") override val absoluteJavaPath: String,
                                                        @get:JsonProperty("isProject") val isProject: Boolean,
                                                        @JsonProperty("canCreateChild") val canCreateChild: Boolean,
                                                        @JsonProperty("hasChildren") val hasChildren: Boolean, // this flag is needed because (for performance reasons), we don't return the entire tree, so "children" may be empty, even though the directory has children
                                                        @JsonProperty("children") val children: List<FileSystemEntry> = emptyList()) : FileSystemEntry

