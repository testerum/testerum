package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

class ZonedDateTimeTypeMeta(@JsonProperty("javaType") override val javaType: String = "java.time.ZonedDateTime") : TypeMeta {
    override fun fileType(): String {
        return "ZONED_DATE_TIME"
    }
}
