package com.testerum.runner_cmdline.transformer.builtin.date

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.common_json.ObjectMapperFactory
import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

object DateTransformer : Transformer<Any> {

    private val objectMapper: ObjectMapper = ObjectMapperFactory.RESOURCE_OBJECT_MAPPER

    override fun canTransform(paramInfo: ParameterInfo): Boolean {
        return (paramInfo.type == java.util.Date::class.java) ||
                (paramInfo.type == Instant::class.java) ||
                (paramInfo.type == LocalDateTime::class.java) ||
                (paramInfo.type == LocalDate::class.java) ||
                (paramInfo.type == ZonedDateTime::class.java)
    }


    override fun transform(toTransform: String, paramInfo: ParameterInfo): Any {
        return objectMapper.readValue("\""+toTransform+"\"", paramInfo.type)
    }
}
