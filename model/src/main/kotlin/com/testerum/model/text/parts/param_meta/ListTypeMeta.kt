package com.testerum.model.text.parts.param_meta

data class ListTypeMeta(
        override val javaType: String,
        val itemsType: TypeMeta
): TypeMeta {
    override fun fileType(): String {
        return "LIST"
    }
}