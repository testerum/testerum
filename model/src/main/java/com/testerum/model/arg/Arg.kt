package com.testerum.model.arg

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.warning.Warning

data class Arg @JsonCreator constructor(@JsonProperty("name") val name: String?,
                                        @JsonProperty("content") val content: String?,
                                        @JsonProperty("type") val type: String,
                                        @JsonProperty("path") val path: Path?,
                                        @JsonProperty("oldPath") val oldPath: Path? = path,
                                        @JsonProperty("warnings") val warnings: List<Warning> = emptyList()) {

    companion object {
        private val MAX_ARG_CONTENT_LENGTH = 40
    }

    private val _descendantsHaveWarnings: Boolean = false

    @get:JsonProperty("descendantsHaveWarnings")
    val descendantsHaveWarnings: Boolean
        get() = _descendantsHaveWarnings

    @get:JsonIgnore
    val hasOwnOrDescendantWarnings: Boolean
        get() = warnings.isNotEmpty() || descendantsHaveWarnings

    @get:JsonIgnore
    val contentForLogging: String?
        get() {
            var result = content

            // remove content of external arg
            if (path != null) {
                result = "file:$path"
            }

            // trim content containing newlines
            val indexOfNewline = result?.indexOf('\n')
            if (result != null && indexOfNewline != null && indexOfNewline != -1) {
                result = result.substring(0, indexOfNewline)
                if (indexOfNewline != result.length - 1) {
                    result += "..."
                }
            }

            // trim long content
            if (result != null && result.length > MAX_ARG_CONTENT_LENGTH) {
                result = result.substring(0, MAX_ARG_CONTENT_LENGTH) + "..."
            }

            return result
        }

    override fun toString(): String = buildString {
        append("Arg(")
        append(" name=[").append(name).append("]")
        append(" type=[").append(type).append("]")
        append(", path=[").append(path).append("]")
        append(", content=[").append(content).append("]")
        append(")")
    }

}
