package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.text.parts.param_meta.field.TypeMetaFieldDescriptor

data class ObjectTypeMeta(
    @JsonProperty("javaType") override val javaType: String,
    @JsonProperty("fields") val fields: List<TypeMetaFieldDescriptor> = emptyList()
) : TypeMeta {

    override fun fileType(): String {
        return "OBJECT"
    }

}
