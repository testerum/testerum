package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonProperty

data class ListTypeMeta(
    @JsonProperty("javaType") override val javaType: String,
    @JsonProperty("itemsType") val itemsType: TypeMeta
) : TypeMeta {

    override fun fileType(): String {
        return "LIST"
    }

}
