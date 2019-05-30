package com.testerum.file_service.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.doesNotExist
import com.testerum.file_service.mapper.business_to_file.BusinessToFileRunConfigMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessRunConfigMapper
import com.testerum.model.runner.config.FileRunConfig
import com.testerum.model.runner.config.RunConfig
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.nio.file.Path as JavaPath

class RunConfigFileService(private val fileToBusinessMapper: FileToBusinessRunConfigMapper,
                           private val businessToFileMapper: BusinessToFileRunConfigMapper) {

    companion object {
        private val LOG = LoggerFactory.getLogger(RunConfigFileService::class.java)

        private const val RUN_CONFIGS_FILENAME = "run-configurations.json"

        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper().apply {
            setDefaultPrettyPrinter(
                    DefaultPrettyPrinter().withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
            )

            registerModule(AfterburnerModule())
            registerModule(KotlinModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            enable(SerializationFeature.INDENT_OUTPUT)

            setSerializationInclusion(JsonInclude.Include.NON_NULL)

            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)

            enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun getRunConfigs(sourceDirectory: JavaPath): List<RunConfig> {
        val file: JavaPath = sourceDirectory.resolve(RUN_CONFIGS_FILENAME)

        if (file.doesNotExist) {
            return emptyList()
        }

        return try {
            val fileRunConfigs = OBJECT_MAPPER.readValue<List<FileRunConfig>>(file.toFile())

            fileRunConfigs.map { fileToBusinessMapper.map(it) }
        } catch (e: Exception) {
            LOG.error("could not load run configurations at [${file.toAbsolutePath().normalize()}]", e)

            emptyList()
        }
    }

    fun save(configs: List<RunConfig>,
             destinationDirectory: JavaPath): List<RunConfig> {
        val fileRunConfigs = configs.map { businessToFileMapper.map(it) }
        val serialized = OBJECT_MAPPER.writeValueAsString(fileRunConfigs)

        val file: JavaPath = destinationDirectory.resolve(RUN_CONFIGS_FILENAME)

        file.parent?.createDirectories()

        Files.write(
                file,
                serialized.toByteArray(Charsets.UTF_8),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )

        return configs
    }

}
