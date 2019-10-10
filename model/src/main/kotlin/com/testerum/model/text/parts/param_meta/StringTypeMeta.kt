package com.testerum.model.text.parts.param_meta

data class StringTypeMeta(override val javaType: String = "java.lang.String") : TypeMeta {
    override fun fileType(): String {
        return "TEXT"
    }
}
