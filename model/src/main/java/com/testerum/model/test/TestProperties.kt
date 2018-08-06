package com.testerum.model.test

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

class TestProperties @JsonCreator constructor(@JsonProperty("isManual")   @get:JsonProperty("isManual")   val isManual: Boolean = false,
                                              @JsonProperty("isDisabled") @get:JsonProperty("isDisabled") val isDisabled: Boolean = false) {

    @JsonIgnore
    fun isEmpty(): Boolean = !isManual && !isDisabled

}