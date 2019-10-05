package com.testerum.model.text.parts.param_meta

class LocalDateTimeTypeMeta(override val javaType: String = "java.time.LocalDateTime") : TypeMeta {
    override fun fileType(): String {
        return "LOCAL_DATE_TIME"
    }
}