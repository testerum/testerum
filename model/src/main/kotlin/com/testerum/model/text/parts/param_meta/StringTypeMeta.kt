package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

data class StringTypeMeta(@JsonProperty("javaType") override val javaType: String = "java.lang.String") : TypeMeta {

    override fun fileType(): String {
        return "TEXT"
    }

}
