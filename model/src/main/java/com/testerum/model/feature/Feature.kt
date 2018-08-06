package com.testerum.model.feature

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.file.Attachment
import com.testerum.model.infrastructure.path.Path

data class Feature @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                            @JsonProperty("name") val name: String,
                                            @JsonProperty("description") val description: String? = null,
                                            @JsonProperty("tags") val tags: List<String> = emptyList(),
                                            @JsonProperty("attachments") val attachments: List<Attachment> = emptyList()) {

    companion object {
        const val FILE_NAME_WITHOUT_EXTENSION: String = "info"
        const val FILE_EXTENSION: String = "feat"
    }

    private val _id = path.toString()

    val id: String
        get() = _id

    override fun toString() = "Feature(name=$name, path=$path)"

}