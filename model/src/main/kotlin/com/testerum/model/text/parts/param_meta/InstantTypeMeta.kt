package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

class InstantTypeMeta(@JsonProperty("javaType") override val javaType: String = "java.time.Instant") : TypeMeta {
    override fun fileType(): String {
        return "INSTANT"
    }
}
