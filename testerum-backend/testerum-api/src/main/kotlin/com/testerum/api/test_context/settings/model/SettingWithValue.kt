package com.testerum.api.test_context.settings.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class SettingWithValue @JsonCreator constructor(
        @JsonProperty("setting")         val setting: Setting,
        @JsonProperty("unresolvedValue") val unresolvedValue: String? = null,
        @JsonProperty("value")           val value: String? = null)