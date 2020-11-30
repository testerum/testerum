package com.testerum.runner.events.model.position

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class PositionInParent @JsonCreator constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("indexInParent") val indexInParent: Int
) {

    override fun toString() = "{$indexInParent: $id}"
}
