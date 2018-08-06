package com.testerum.web_backend.controller.config.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.api.test_context.settings.model.Setting
import com.testerum.api.test_context.settings.model.SettingType
import com.testerum.api.test_context.settings.model.SettingWithValue

data class UiSetting @JsonCreator constructor(
        @JsonProperty("key")             val key: String,
        @JsonProperty("unresolvedValue") val unresolvedValue: String? = null,
        @JsonProperty("value")           val value: String? = null,
        @JsonProperty("type")            val type: SettingType = SettingType.TEXT,
        @JsonProperty("defaultValue")    val defaultValue: String? = null,
        @JsonProperty("description")     val description: String? = null,
        @JsonProperty("category")        val category: String? = null) {

    fun toSettingWithValue(): SettingWithValue {
        return SettingWithValue(

                setting = Setting(
                        key = this.key,
                        type = this.type,
                        defaultValue = this.defaultValue,
                        description = this.description,
                        category = this.category
                ),
                unresolvedValue = this.unresolvedValue,
                value = this.value
        )
    }
}

fun SettingWithValue.toUiSetting(): UiSetting {
    return UiSetting(
            key = this.setting.key,
            type = this.setting.type,
            defaultValue = this.setting.defaultValue,
            description = this.setting.description,
            category = this.setting.category,
            unresolvedValue = this.unresolvedValue,
            value = this.value
    )
}