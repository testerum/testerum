package net.qutester.model.feature

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.file.Attachment
import net.qutester.model.infrastructure.path.Path

data class Feature @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                            @JsonProperty("name") val name: String,
                                            @JsonProperty("description") val description: String? = null,
                                            @JsonProperty("tags") val tags: List<String> = emptyList(),
                                            @JsonProperty("attachments") val attachments: List<Attachment> = emptyList()) {

    private val _id = path.toString()

    val id: String
        get() = _id

}