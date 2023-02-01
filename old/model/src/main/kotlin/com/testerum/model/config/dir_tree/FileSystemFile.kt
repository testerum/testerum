package com.testerum.model.config.dir_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FileSystemFile @JsonCreator constructor(@JsonProperty("name") override val name: String,
                                                   @JsonProperty("absoluteJavaPath") override val absoluteJavaPath: String) : FileSystemEntry

