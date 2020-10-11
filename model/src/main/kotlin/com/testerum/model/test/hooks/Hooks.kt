package com.testerum.model.test.hooks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.step.StepCall

data class Hooks @JsonCreator constructor(
    @JsonProperty("beforeAll") val beforeAll: List<StepCall> = emptyList(),
    @JsonProperty("beforeEach") val beforeEach: List<StepCall> = emptyList(),
    @JsonProperty("afterEach") val afterEach: List<StepCall> = emptyList(),
    @JsonProperty("afterAll") val afterAll: List<StepCall> = emptyList(),
) {

    companion object {
        val EMPTY = Hooks()
    }

}
