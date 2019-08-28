package com.testerum.model.text.parts.param_meta.type

import com.testerum.model.text.parts.param_meta.FieldTypeMeta

data class ObjectTypeMeta(
        val javaType: String,
        val fields: List<FieldTypeMeta> = emptyList()
): TypeMeta