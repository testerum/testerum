package com.testerum.api.test_context.settings.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Setting @JsonCreator constructor(
        @JsonProperty("key")             val key: String,
        @JsonProperty("type")            val type: SettingType = SettingType.TEXT,
        @JsonProperty("defaultValue")    val defaultValue: String? = null,
        @JsonProperty("description")     val description: String? = null,
        @JsonProperty("category")        val category: String? = null
)
