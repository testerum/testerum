package com.testerum.model.feature

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.file.Attachment
import com.testerum.model.infrastructure.path.Path

data class Feature @JsonCreator constructor(@JsonProperty("name") val name: String,
                                            @JsonProperty("path") val path: Path,
                                            @JsonProperty("oldPath") val oldPath: Path? = path,
                                            @JsonProperty("description") val description: String? = null,
                                            @JsonProperty("tags") val tags: List<String> = emptyList(),
                                            @JsonProperty("attachments") val attachments: List<Attachment> = emptyList()) {

    companion object {
        const val FILE_NAME_WITHOUT_EXTENSION: String = "info"
        const val FILE_EXTENSION: String = "feat"
        const val FILE_NAME_WITH_EXTENSION: String = FILE_NAME_WITHOUT_EXTENSION + "." + FILE_EXTENSION
    }

    private val _id = path.toString()

    val id: String
        get() = _id

    @JsonIgnore
    fun getNewPath(): Path {
        val directories = path.directories
        val newDirectories = mutableListOf<String>()

        if (directories.isNotEmpty()) {
            newDirectories.addAll(
                    directories.subList(0, directories.size - 1)
            )
        }
        newDirectories.add(name)

        return path.copy(
                directories = newDirectories
        )
    }

    override fun toString() = "Feature(name=$name, path=$path)"

}