package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

data class DateTypeMeta(@JsonProperty("javaType") override val javaType: String = "java.util.Date") : TypeMeta {

    override fun fileType(): String {
        return "DATE"
    }

}
