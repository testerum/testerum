package com.testerum.model.config.dir_tree

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class CreateFileSystemDirectoryRequest @JsonCreator constructor(@JsonProperty("parentAbsoluteJavaPath") val parentAbsoluteJavaPath: String,
                                                                @JsonProperty("name") val name: String)
