package com.testerum.model.text.parts.param_meta

class DateTypeMeta(override val javaType: String = "java.util.Date") : TypeMeta {
    override fun fileType(): String {
        return "DATE"
    }
}