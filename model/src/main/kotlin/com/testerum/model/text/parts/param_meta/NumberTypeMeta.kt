package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

data class NumberTypeMeta(@JsonProperty("javaType") override val javaType: String) : TypeMeta {

    override fun fileType(): String {
        return "NUMBER"
    }

}
