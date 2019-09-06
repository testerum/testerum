package com.testerum.model.text.parts.param_meta

import com.testerum.model.text.parts.param_meta.field.FieldTypeMeta

data class ObjectTypeMeta (
        override val javaType: String,
        val fields: List<FieldTypeMeta> = emptyList()
): TypeMeta {
    override fun fileType(): String {
        return "OBJECT"
    }
}