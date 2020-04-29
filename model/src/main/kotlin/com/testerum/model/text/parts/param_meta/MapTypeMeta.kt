package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

data class MapTypeMeta(
        @JsonProperty("javaType") override val javaType: String,
        @JsonProperty("keyType") val keyType: TypeMeta,
        @JsonProperty("valueType") val valueType: TypeMeta
): TypeMeta {
    override fun fileType(): String {
        return "MAP"
    }
}
