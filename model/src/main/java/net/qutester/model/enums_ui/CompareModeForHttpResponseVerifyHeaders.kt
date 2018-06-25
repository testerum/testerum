package net.qutester.model.enums_ui

import com.fasterxml.jackson.annotation.JsonProperty

enum class CompareModeForHttpResponseVerifyHeaders: UiConfig {

    EXACT,
    CONTAINS,
    MATCHES_REGEX;

    override val key: String
        @JsonProperty("key") get() = this.name

    override val values: List<String>
        @JsonProperty("values") get() = emptyList()
}