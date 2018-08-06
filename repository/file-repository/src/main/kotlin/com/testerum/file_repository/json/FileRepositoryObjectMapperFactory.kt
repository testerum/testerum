package com.testerum.file_repository.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


object FileRepositoryObjectMapperFactory {

    @JvmStatic
    fun createKotlinObjectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()

        setOptions(objectMapper)

        return objectMapper
    }

    private fun setOptions(objectMapper: ObjectMapper) {
        objectMapper.registerModule(AfterburnerModule())
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.registerModule(GuavaModule())

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

}