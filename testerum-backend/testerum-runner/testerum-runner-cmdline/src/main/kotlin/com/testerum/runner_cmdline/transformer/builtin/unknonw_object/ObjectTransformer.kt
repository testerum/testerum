package com.testerum.runner_cmdline.transformer.builtin.unknonw_object

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.common_json.ObjectMapperFactory

object ObjectTransformer : Transformer<Any> {

    private val objectMapper: ObjectMapper = ObjectMapperFactory.RESOURCE_OBJECT_MAPPER

    override fun canTransform(paramInfo: ParameterInfo): Boolean = true

    override fun transform(toTransform: String, paramInfo: ParameterInfo): Any {
        return objectMapper.readValue(toTransform, paramInfo.type)
    }
}