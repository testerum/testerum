package com.testerum.file_service.file

import com.dslplatform.json.DslJson
import com.dslplatform.json.runtime.Settings
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
import com.testerum.common_kotlin.exists
import com.testerum.common_kotlin.isNotADirectory
import com.testerum.common_kotlin.writeText
import com.testerum.model.exception.ValidationException
import com.testerum.model.project.FileProject
import com.testerum.model.project.FileProjectV1
import org.slf4j.LoggerFactory
import java.util.*
import java.nio.file.Path as JavaPath

class TesterumProjectFileService {

    companion object {
        const val TESTERUM_PROJECT_DIR = ".testerum"

        private val LOG = LoggerFactory.getLogger(TesterumProjectFileService::class.java)

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

    fun save(fileProject: FileProject, projectRootDir: JavaPath): FileProject {
        val serializedProject = OBJECT_MAPPER.writeValueAsString(fileProject)

        val projectFile = projectFilePath(projectRootDir)
        projectFile.parent?.createDirectories()
        projectFile.writeText(serializedProject)

        return fileProject
    }

    fun load(projectRootDir: JavaPath): FileProject {
        if (!isTesterumProject(projectRootDir)) {
            throw ValidationException(
                    globalMessage = "The directory [${projectRootDir.toAbsolutePath().normalize()}] is not a Testerum project.",
                    globalHtmlMessage = "The directory<br/><code>${projectRootDir.toAbsolutePath().normalize()}</code><br/>is not a Testerum project."
            )
        }

        try {
            val projectFile = projectFilePath(projectRootDir)

            // first try to load the current version
            val fileProject = loadFileProjectCurrentVersionSafely(projectFile)
            if (fileProject != null) {
                return fileProject
            }

            // failed to load current version, try to load old version of the project and convert
            val fileProjectV1 = loadFileProjectV1(projectFile)
            val convertedFileProject = convertOldToNewFileProjectFormat(fileProjectV1)
            save(convertedFileProject, projectRootDir)

            return convertedFileProject
        } catch (e: Exception) {
            val errorMessage = "Failed to load [${projectRootDir.toAbsolutePath().normalize()}] as a Testerum project"
            LOG.error(errorMessage, e)

            throw ValidationException(errorMessage)
        }
    }

    private fun loadFileProjectCurrentVersionSafely(projectFile: JavaPath): FileProject? {
        return try {
            val dslJson = DslJson<Any>(Settings.withRuntime<Any>().includeServiceLoader())
            dslJson.deserialize(FileProject::class.java, projectFile.toFile().inputStream())
        } catch (e: Exception) {
            null
        }
    }

    private fun loadFileProjectV1(projectFile: JavaPath): FileProjectV1 {
        return OBJECT_MAPPER.readValue(projectFile.toFile())
    }

    private fun convertOldToNewFileProjectFormat(fileProjectV1: FileProjectV1): FileProject {
        return FileProject(
                name = fileProjectV1.name,
                id = generateProjectId()
        )
    }

    fun isTesterumProject(projectRootDir: JavaPath): Boolean {
        if (projectRootDir.doesNotExist) {
            return false
        }
        if (projectRootDir.isNotADirectory) {
            return false
        }

        val projectFile = projectFilePath(projectRootDir)

        return projectFile.exists
    }

    private fun projectFilePath(projectRootDir: JavaPath): JavaPath {
        return projectRootDir.resolve(TESTERUM_PROJECT_DIR).resolve("project.json")
    }

    fun generateProjectId(): String = UUID.randomUUID().toString()

}
