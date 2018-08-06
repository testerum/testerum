package com.testerum.service.mapper.file_arg_transformer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule

object FileArgTransformer {

    private val ARG_TYPES_TO_TRANSFORM: Set<String> = setOf(
            "com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig",

            "com.testerum.model.resources.http.request.HttpRequest",
            "http.response.verify.model.HttpResponseVerify",

            "com.testerum.model.resources.http.mock.server.HttpMockServer",
            "com.testerum.model.resources.http.mock.stub.HttpMock"
    )

    private val JSON_MAPPER: ObjectMapper = ObjectMapper().apply {
        registerModule(AfterburnerModule())
        registerModule(JavaTimeModule())
        registerModule(GuavaModule())

        disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    private val YAML_MAPPER: ObjectMapper = run {
        val factory = YAMLFactory()

        factory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
        factory.disable(YAMLGenerator.Feature.USE_PLATFORM_LINE_BREAKS)

        factory.enable(YAMLGenerator.Feature.SPLIT_LINES)
        factory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        factory.enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
        factory.enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
        factory.enable(YAMLGenerator.Feature.INDENT_ARRAYS)

        ObjectMapper(factory).apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun jsonToFileFormat(text: String, argType: String?): String {
        if (!shouldTransform(argType)) {
            return text
        }

        val jsonTree: JsonNode = JSON_MAPPER.readTree(text)

        return YAML_MAPPER.writeValueAsString(jsonTree)
    }

    fun fileFormatToJson(text: String, argType: String?): String {
        if (!shouldTransform(argType)) {
            return text
        }

        val jsonTree: JsonNode
        try {
            jsonTree = YAML_MAPPER.readTree(text)
        } catch (e: Exception) {
            throw e
        }

        return JSON_MAPPER.writeValueAsString(jsonTree)
    }

    private fun shouldTransform(argType: String?): Boolean {
        return argType != null
                && ARG_TYPES_TO_TRANSFORM.contains(argType)
    }

}
