package com.testerum.common_json.util

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object JsonUtils {

    private val OBJECT_MAPPER = jacksonObjectMapper().apply {
        registerModule(AfterburnerModule())
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    fun prettyPrintJson(json: String): String {
        val jsonObject = OBJECT_MAPPER.readValue<Any>(json)

        return OBJECT_MAPPER.writeValueAsString(jsonObject)
    }

}
