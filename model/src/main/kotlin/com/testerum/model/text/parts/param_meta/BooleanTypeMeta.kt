package com.testerum.model.text.parts.param_meta

class BooleanTypeMeta(override val javaType: String = "java.lang.Boolean") : TypeMeta {
    override fun fileType(): String {
        return "BOOLEAN";
    }
}