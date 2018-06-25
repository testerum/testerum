package net.qutester.model.file

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path
import java.time.LocalDateTime


data class Attachment @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                               @JsonProperty("mimeType") val mimeType: String?,
                                               @JsonProperty("size") val size: Long,
                                               @JsonProperty("lastModifiedDate") val lastModifiedDate: LocalDateTime) {
}