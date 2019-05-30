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
import com.testerum.common_kotlin.smartMoveTo
import com.testerum.common_kotlin.walk
import com.testerum.file_service.mapper.business_to_file.BusinessToFileRunConfigMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessRunConfigMapper
import com.testerum.model.exception.ValidationException
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.config.FileRunConfig
import com.testerum.model.runner.config.RunConfig
import com.testerum.model.util.escape
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.nio.file.Path as JavaPath

class RunConfigFileService(private val fileToBusinessMapper: FileToBusinessRunConfigMapper,
                           private val businessToFileMapper: BusinessToFileRunConfigMapper) {

    companion object {
        private val LOG = LoggerFactory.getLogger(RunConfigFileService::class.java)

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

    fun getAllRunConfigs(projectRootDir: JavaPath): List<RunConfig> {
        val runConfigs = mutableListOf<RunConfig>()

        val runConfigsDir = runConfigsDir(projectRootDir).toAbsolutePath().normalize()
        runConfigsDir.walk { path ->
            val fileRunConfig = parseRunConfigFile(path)

            if (fileRunConfig != null) {
                val relativePath: JavaPath = runConfigsDir.relativize(path)
                val runConfig = fileToBusinessMapper.mapRunConfig(fileRunConfig, relativePath)

                runConfigs += runConfig
            }
        }

        return runConfigs
    }

    private fun parseRunConfigFile(runConfigFile: JavaPath): FileRunConfig? {
        return try {
            OBJECT_MAPPER.readValue(runConfigFile.toFile())
        } catch (e: Exception) {
            LOG.error("could not load run configuration at [${runConfigFile.toAbsolutePath().normalize()}]", e)

            null
        }
    }

    fun save(config: RunConfig, projectRootDir: JavaPath): RunConfig {
        val oldPath: Path? = config.oldPath
        val newEscapedPath: Path = config.getNewPath().escape()

        val oldFile: JavaPath? = oldPath?.let {
            runConfigsDir(projectRootDir)
                    .resolve(it.toString())
                    .toAbsolutePath()
                    .normalize()
        }
        val newFile: JavaPath = runConfigsDir(projectRootDir)
                .resolve(newEscapedPath.toString())
                .toAbsolutePath()
                .normalize()

        // handle rename
        oldFile?.smartMoveTo(
                newFile,
                createDestinationExistsException = {
                    val dirPath = newEscapedPath.copy(fileName = null, fileExtension = null)

                    ValidationException(
                            globalMessage = "The run configuration at path [$dirPath] already exists",
                            globalHtmlMessage = "The run configuration at path<br/><code>$dirPath</code><br/>already exists"
                    )
                }
        )

        // write the new file

        val fileRunConfig = businessToFileMapper.mapRunConfig(config)
        val serialized = OBJECT_MAPPER.writeValueAsString(fileRunConfig)

        newFile.parent?.createDirectories()
        Files.write(
                newFile,
                serialized.toByteArray(Charsets.UTF_8),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )

        val newPath = Path.createInstance(
                runConfigsDir(projectRootDir)
                        .relativize(newFile)
                        .toString()
        )

        return config.copy(
                path = newPath,
                oldPath = newPath
        )
    }

    private fun runConfigsDir(projectRootDir: JavaPath): JavaPath {
        return projectRootDir.resolve(TesterumProjectFileService.TESTERUM_PROJECT_DIR).resolve("run-configs")
    }

}
