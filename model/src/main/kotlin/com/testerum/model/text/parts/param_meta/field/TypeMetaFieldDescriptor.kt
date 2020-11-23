package com.testerum.model.text.parts.param_meta.field

import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.text.parts.param_meta.TypeMeta

data class TypeMetaFieldDescriptor(
    @JsonProperty("name") val name: String,
    @JsonProperty("type") val type: TypeMeta
)
