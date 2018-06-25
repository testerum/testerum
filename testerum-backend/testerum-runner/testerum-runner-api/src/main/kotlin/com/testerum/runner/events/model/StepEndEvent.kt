package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.runner.events.model.error.ExceptionDetail
import com.testerum.runner.events.model.position.EventKey
import com.testerum.api.test_context.ExecutionStatus
import net.qutester.model.step.StepCall
import java.time.LocalDateTime

data class StepEndEvent @JsonCreator constructor(
        @JsonProperty("time")            override val time: LocalDateTime = LocalDateTime.now(),
        @JsonProperty("eventKey")        override val eventKey: EventKey,
        @JsonProperty("stepCall")        val stepCall: StepCall,
        @JsonProperty("status")          val status: ExecutionStatus,
        @JsonProperty("exceptionDetail") val exceptionDetail: ExceptionDetail? = null,
        @JsonProperty("durationMillis")  val durationMillis: Long
): RunnerEvent {
    @JsonProperty("exceptionDetailAsString")
    fun exceptionDetailAsString(): String? {
        return exceptionDetail?.detailedToString()
    }
}
