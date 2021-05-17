package com.testerum.model.feature.hooks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
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

    @JsonIgnore
    fun hasHooks(): Boolean {
        return beforeAll.isNotEmpty()
            || beforeEach.isNotEmpty()
            || afterEach.isNotEmpty()
            || afterAll.isNotEmpty()
    }

    fun getByPhase(phase: HookPhase): List<StepCall> {
        return when (phase) {
            HookPhase.BEFORE_ALL_TESTS -> beforeAll
            HookPhase.BEFORE_EACH_TEST -> beforeEach
            HookPhase.AFTER_EACH_TEST -> afterEach
            HookPhase.AFTER_ALL_TESTS -> afterAll
            HookPhase.AFTER_TEST -> emptyList()
        }
    }
}
