package com.testerum.file_service.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.doesNotExist
import com.testerum.model.variable.FileLocalVariables
import com.testerum.model.variable.FileProjectLocalVariables
import java.util.*
import java.nio.file.Path as JavaPath

class LocalVariablesFileService {

    companion object {
        private val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            enable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }

    }

    fun save(fileLocalVariables: FileLocalVariables,
             fileLocalVariablesFile: JavaPath): FileLocalVariables {
        fileLocalVariablesFile.parent?.createDirectories()

        OBJECT_MAPPER.writeValue(
                fileLocalVariablesFile.toFile(),
                fileLocalVariables.projectLocalVariables
        )

        return fileLocalVariables
    }

    fun load(fileLocalVariablesFile: JavaPath): FileLocalVariables {
        if (fileLocalVariablesFile.doesNotExist) {
            return FileLocalVariables.EMPTY
        }

        val projectLocalVariables: TreeMap<String, FileProjectLocalVariables> = OBJECT_MAPPER.readValue(
                fileLocalVariablesFile.toFile()
        )

        return FileLocalVariables(projectLocalVariables)
    }

}
