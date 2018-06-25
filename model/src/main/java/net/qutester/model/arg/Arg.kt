package net.qutester.model.arg

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.infrastructure.path.Path

data class Arg @JsonCreator constructor(@JsonProperty("name") val name: String?,
                                        @JsonProperty("content") val content: String,
                                        @JsonProperty("type") val type: String,
                                        @JsonProperty("path") val path: Path?) {

    override fun toString(): String = buildString {
        append("Arg(")
        append(" type=[").append(type).append("]")
        append(", path=[").append(path).append("]")
        append(", content=[").append(content).append("]")
        append(")")
    }

}