package com.testerum.common_json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ObjectMapperFactory {

    // todo: reduce number of mappers

    val RESOURCE_OBJECT_MAPPER: ObjectMapper by lazy { createKotlinObjectMapperWithPrettyFormatter() }
    val RUNNER_EVENT_OBJECT_MAPPER: ObjectMapper by lazy { createRunnerEventObjectMapper() }
    val VARIABLES_OBJECT_MAPPER: ObjectMapper by lazy { createObjectMapper() }
    val WEB_OBJECT_MAPPER: ObjectMapper by lazy { createObjectMapper() }

    fun createObjectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()

        objectMapper.registerModule(AfterburnerModule())
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.registerModule(GuavaModule())

        objectMapper.disable(SerializationFeature.INDENT_OUTPUT)
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        return objectMapper
    }

    fun createKotlinObjectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()

        objectMapper.registerModule(AfterburnerModule())
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.registerModule(GuavaModule())

        objectMapper.disable(SerializationFeature.INDENT_OUTPUT)
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        return objectMapper
    }

    fun createKotlinObjectMapperWithPrettyFormatter(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()

        objectMapper.registerModule(AfterburnerModule())
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.registerModule(GuavaModule())

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        return objectMapper
    }

    private fun createRunnerEventObjectMapper() = jacksonObjectMapper().apply {
        registerModule(AfterburnerModule())
        registerModule(JavaTimeModule())
        registerModule(GuavaModule())

        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

        disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

}
