package com.testerum.model.step.filter

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class StepsTreeFilter @JsonCreator constructor(@JsonProperty("showComposedTests") val showComposedTests: Boolean = true, // todo: rename to 'showComposedSteps'
                                                    @JsonProperty("showBasicTest") val showBasicTest: Boolean = true,         // todo: rename to 'showBasicSteps'
                                                    @JsonProperty("search") val search: String? = null,
                                                    @JsonProperty("tags") val tags: List<String> = emptyList())
