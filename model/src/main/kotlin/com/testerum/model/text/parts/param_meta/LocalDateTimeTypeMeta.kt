package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

data class LocalDateTimeTypeMeta(@JsonProperty("javaType") override val javaType: String = "java.time.LocalDateTime") : TypeMeta {

    override fun fileType(): String {
        return "LOCAL_DATE_TIME"
    }

}
