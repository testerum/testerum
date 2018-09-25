package com.testerum.model.feature.filter

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FeaturesTreeFilter @JsonCreator constructor(@JsonProperty("includeAutomatedTests") val includeAutomatedTests: Boolean = true,
                                                       @JsonProperty("includeManualTests") val includeManualTests: Boolean = true,
                                                       @JsonProperty("includeEmptyFeatures") val includeEmptyFeatures: Boolean = true,
                                                       @JsonProperty("search") val search: String?,
                                                       @JsonProperty("tags") val tags: List<String> = emptyList())
