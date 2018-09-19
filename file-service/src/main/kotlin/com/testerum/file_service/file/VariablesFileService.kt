package com.testerum.file_service.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.doesNotExist
import com.testerum.model.variable.Variable
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.nio.file.Path as JavaPath

class VariablesFileService {

    companion object {
        private const val VARIABLES_FILENAME = "variables.json"

        private val OBJECT_MAPPER = ObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            disable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun save(variables: List<Variable>,
             variablesDir: JavaPath): List<Variable> {
        val variablesFile = variablesDir.resolve(VARIABLES_FILENAME)

        val variablesMap = variables.associateBy({ it.key }, { it.value })
        val variablesFileContent = OBJECT_MAPPER.writeValueAsBytes(variablesMap)

        // todo: we should't write all variables at once, to minimize conflicts with other users

        variablesFile.parent?.createDirectories()

        Files.write(
                variablesFile,
                variablesFileContent,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )

        return variables
    }

    fun getVariables(variablesDir: JavaPath): List<Variable> {
        val variablesAsMap = getVariablesAsMap(variablesDir)

        return variablesAsMap.map {
            Variable(it.key, it.value)
        }
    }

    fun getVariablesAsMap(variablesDir: JavaPath): Map<String, String> {
        val variablesFile = variablesDir.resolve(VARIABLES_FILENAME)

        if (variablesFile.doesNotExist) {
            return emptyMap()
        }

        return OBJECT_MAPPER.readValue(variablesFile.toFile())
    }


}
