package com.testerum.model.text.parts.param_meta

class NumberTypeMeta(override val javaType: String) : TypeMeta {
    override fun fileType(): String {
        return "NUMBER"
    }
}