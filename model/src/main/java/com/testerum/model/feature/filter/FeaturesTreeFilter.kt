package com.testerum.model.feature.filter

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FeaturesTreeFilter @JsonCreator constructor(@JsonProperty("showAutomatedTests") val showAutomatedTests: Boolean = true,
                                                       @JsonProperty("showManualTest") val showManualTest: Boolean = true, // todo: rename to "showManualTests"
                                                       @JsonProperty("showEmptyFeatures") val showEmptyFeatures: Boolean = true,
                                                       @JsonProperty("search") val search: String?,
                                                       @JsonProperty("tags") val tags: List<String> = emptyList())
