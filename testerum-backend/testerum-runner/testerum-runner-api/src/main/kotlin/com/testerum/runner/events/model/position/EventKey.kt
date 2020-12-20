package com.testerum.runner.events.model.position

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class EventKey @JsonCreator constructor(@JsonProperty("positionsFromRoot") val positionsFromRoot: List<PositionInParent>) {

    companion object {
        val SUITE_POSITION_IN_PARENT = PositionInParent(id = "TestSuite", indexInParent = 0)
        val SUITE_EVENT_KEY = EventKey(
            listOf(PositionInParent(id = "TestSuite", indexInParent = 0))
        )
        val LOG_EVENT_KEY = EventKey(
            listOf()
        )
    }

    override fun toString() = positionsFromRoot.toString()

}
