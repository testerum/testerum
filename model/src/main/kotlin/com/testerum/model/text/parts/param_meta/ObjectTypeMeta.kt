package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.text.parts.param_meta.field.FieldTypeMeta

data class ObjectTypeMeta (
        @JsonProperty("javaType") override val javaType: String,
        @JsonProperty("fields") val fields: List<FieldTypeMeta> = emptyList()
): TypeMeta {
    override fun fileType(): String {
        return "OBJECT"
    }
}
