package com.testerum.model.resources.http.mock.stub.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class HttpMockRequestScenario @JsonCreator constructor(
        @JsonProperty("scenarioName") var scenarioName: String,
        @JsonProperty("currentState") var currentState: String,
        @JsonProperty("newState") var newState: String?
)
