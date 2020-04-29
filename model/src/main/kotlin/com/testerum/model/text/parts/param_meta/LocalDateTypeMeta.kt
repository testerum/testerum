package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

class LocalDateTypeMeta(@JsonProperty("javaType") override val javaType: String = "java.time.LocalDate") : TypeMeta {
    override fun fileType(): String {
        return "LOCAL_DATE"
    }
}
