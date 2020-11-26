package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

data class EnumTypeMeta(
    @JsonProperty("javaType") override val javaType: String = "ENUM",
    @JsonProperty("possibleValues") val possibleValues: List<String> = emptyList()
) : TypeMeta {

    override fun fileType(): String {
        return "ENUM"
    }

}
