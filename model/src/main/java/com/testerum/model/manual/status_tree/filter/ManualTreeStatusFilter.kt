package com.testerum.model.manual.status_tree.filter

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ManualTreeStatusFilter @JsonCreator constructor(@JsonProperty("showNotExecuted") val showNotExecuted: Boolean = true,
                                                           @JsonProperty("showPassed") val showPassed: Boolean = true,
                                                           @JsonProperty("showFailed") val showFailed: Boolean = true,
                                                           @JsonProperty("showBlocked") val showBlocked: Boolean = true,
                                                           @JsonProperty("showNotApplicable") val showNotApplicable: Boolean = true,
                                                           @JsonProperty("search") val search: String?,
                                                           @JsonProperty("tags") val tags: List<String> = emptyList()) {
    override fun toString(): String {
        return "ManualTreeStatusFilter(showNotExecuted=$showNotExecuted, " +
                "showPassed=$showPassed, " +
                "showFailed=$showFailed, " +
                "showBlocked=$showBlocked, " +
                "showNotApplicable=$showNotApplicable, " +
                "search=$search, " +
                "tags=$tags)"
    }
}