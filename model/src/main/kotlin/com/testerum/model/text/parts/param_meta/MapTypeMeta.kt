package com.testerum.model.text.parts.param_meta

data class MapTypeMeta(
        override val javaType: String,
        val keyType: TypeMeta,
        val valueType: TypeMeta
): TypeMeta {
    override fun fileType(): String {
        return "MAP"
    }
}