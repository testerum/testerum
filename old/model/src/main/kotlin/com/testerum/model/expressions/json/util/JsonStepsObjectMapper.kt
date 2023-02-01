package com.testerum.model.expressions.json.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val JSON_STEPS_OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
    registerModule(AfterburnerModule())
    registerModule(JavaTimeModule())

    disable(SerializationFeature.INDENT_OUTPUT)
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

    disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
}

