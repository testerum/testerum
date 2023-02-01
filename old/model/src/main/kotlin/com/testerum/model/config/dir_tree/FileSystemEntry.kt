package com.testerum.model.config.dir_tree

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = FileSystemFile::class      , name = "FILE"),
    JsonSubTypes.Type(value = FileSystemDirectory::class , name = "DIR")
])
interface FileSystemEntry {

    val name: String
    val absoluteJavaPath: String

}
