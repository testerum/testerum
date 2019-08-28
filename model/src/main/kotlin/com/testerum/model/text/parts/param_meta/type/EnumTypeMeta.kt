package com.testerum.model.text.parts.param_meta.type

data class EnumTypeMeta (
        val javaType: String,
        val possibleValues: List<String> = emptyList()
): TypeMeta