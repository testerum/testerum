package com.testerum.runner.events.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.runner.events.model.error.ExceptionDetail
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import java.time.LocalDateTime

data class TextLogEvent @JsonCreator constructor(
        @JsonProperty("time")            override val time: LocalDateTime,
        @JsonProperty("eventKey")        override val eventKey: EventKey,

        @JsonProperty("logLevel")        val logLevel: LogLevel,
        @JsonProperty("message")         val message: String,
        @JsonProperty("exceptionDetail") val exceptionDetail: ExceptionDetail?
): RunnerEvent
