package com.testerum.model.text.parts.param_meta

data class EnumTypeMeta (
        override val javaType: String = "ENUM",
        val possibleValues: List<String> = emptyList()
): TypeMeta {
    override fun fileType(): String {
        return "ENUM"
    }
}