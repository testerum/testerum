package com.testerum.common_json.util

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

private val JSON_UTILS_OBJECT_MAPPER = jacksonObjectMapper().apply {
    registerModule(AfterburnerModule())
    enable(SerializationFeature.INDENT_OUTPUT)
}

fun String?.prettyPrintJson(): String {
    if (this == null) {
        return "null"
    }

    val jsonTree = JSON_UTILS_OBJECT_MAPPER.readTree(this)

    return JSON_UTILS_OBJECT_MAPPER.writeValueAsString(jsonTree)
}
