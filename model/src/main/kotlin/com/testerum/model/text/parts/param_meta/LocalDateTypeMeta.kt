package com.testerum.model.text.parts.param_meta

class LocalDateTypeMeta(override val javaType: String = "java.time.LocalDate") : TypeMeta {
    override fun fileType(): String {
        return "LOCAL_DATE"
    }
}