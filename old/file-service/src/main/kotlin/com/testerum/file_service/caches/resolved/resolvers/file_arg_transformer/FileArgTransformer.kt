package com.testerum.file_service.caches.resolved.resolvers.file_arg_transformer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.testerum.model.text.parts.param_meta.ListTypeMeta
import com.testerum.model.text.parts.param_meta.MapTypeMeta
import com.testerum.model.text.parts.param_meta.ObjectTypeMeta
import com.testerum.model.text.parts.param_meta.TypeMeta

object FileArgTransformer {

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

    fun jsonToFileFormat(text: String, argType: TypeMeta): String {
        if (!shouldTransform(argType)) {
            return text
        }

        val jsonTree: JsonNode? = JSON_MAPPER.readTree(text)

        if(jsonTree == null) return ""

        return YAML_MAPPER.writeValueAsString(jsonTree)
    }

    fun fileFormatToJson(text: String, argTypeMeta: TypeMeta): String? {
        if (!shouldTransform(argTypeMeta)) {
            return text
        }
        if (text == "") {
            return null
        }

        val jsonTree: JsonNode
        try {
            jsonTree = YAML_MAPPER.readTree(text)
        } catch (e: Exception) {
            throw e
        }

        return JSON_MAPPER.writeValueAsString(jsonTree)
    }

    fun shouldTransform(argTypeMeta: TypeMeta): Boolean {
        if(argTypeMeta is ObjectTypeMeta) {
            when (argTypeMeta.javaType) {
                "database.relational.model.RdbmsSql" -> return false;
                "json.model.JsonResource" -> return false;
                "json.model.JsonVerifyResource" -> return false;
            }
        }
        return argTypeMeta is ObjectTypeMeta ||
               argTypeMeta is ListTypeMeta ||
               argTypeMeta is MapTypeMeta
    }
}
