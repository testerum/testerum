package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

data class BooleanTypeMeta(@JsonProperty("javaType") override val javaType: String = "java.lang.Boolean") : TypeMeta {

    override fun fileType(): String {
        return "BOOLEAN"
    }

}
