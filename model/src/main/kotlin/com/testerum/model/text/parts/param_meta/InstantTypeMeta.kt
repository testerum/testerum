package com.testerum.model.text.parts.param_meta

class InstantTypeMeta(override val javaType: String = "java.time.Instant") : TypeMeta {
    override fun fileType(): String {
        return "INSTANT"
    }
}