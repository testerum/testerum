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
import com.testerum.model.variable.ProjectVariables
import com.testerum.model.variable.ReservedVariableEnvironmentNames
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

            enable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun saveProjectVariables(projectVariables: ProjectVariables,
                             projectVariablesDir: JavaPath): ProjectVariables {
        val variablesFile = projectVariablesDir.resolve(VARIABLES_FILENAME)

        val variablesFileContent = OBJECT_MAPPER.writeValueAsBytes(projectVariables)

        variablesFile.parent?.createDirectories()

        Files.write(
                variablesFile,
                variablesFileContent,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )

        return projectVariables
    }

    fun getProjectVariables(projectVariablesDir: JavaPath): ProjectVariables {
        val variablesFile = projectVariablesDir.resolve(VARIABLES_FILENAME)

        if (variablesFile.doesNotExist) {
            return ProjectVariables.EMPTY
        }

        return OBJECT_MAPPER.readValue(variablesFile.toFile())
    }

    fun getMergedVariables(projectVariablesDir: JavaPath,
                           currentEnvironment: String?,
                           variableOverrides: Map<String, String>): Map<String, String> {
        val result = HashMap<String, String>()

        val projectVariables = getProjectVariables(projectVariablesDir)

        // 1. default environment
        result.putAll(projectVariables.defaultVariables)

        // 2. current environment (from project or local)
        if ((currentEnvironment != null) && (currentEnvironment != ReservedVariableEnvironmentNames.DEFAULT)) {
            if (currentEnvironment == ReservedVariableEnvironmentNames.LOCAL) {
                throw IllegalArgumentException("NOT YET IMPLEMENTED: local environment")
            } else {
                val environment = projectVariables.environments[currentEnvironment]
                        ?: throw IllegalArgumentException("the environment [$currentEnvironment] does not exist in this project")

                result.putAll(environment)
            }
        }

        // 3. variable overrides
        result.putAll(variableOverrides)

        return result
    }
}
