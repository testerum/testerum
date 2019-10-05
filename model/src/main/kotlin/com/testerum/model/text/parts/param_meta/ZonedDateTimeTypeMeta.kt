package com.testerum.model.text.parts.param_meta

class ZonedDateTimeTypeMeta(override val javaType: String = "java.time.ZonedDateTime") : TypeMeta {
    override fun fileType(): String {
        return "ZONED_DATE_TIME"
    }
}