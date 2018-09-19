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
import com.testerum.common_kotlin.*
import com.testerum.file_service.caches.resolved.resolvers.file_arg_transformer.FileArgTransformer
import com.testerum.file_service.file.util.escape
import com.testerum.file_service.file.util.escapeFileOrDirectoryName
import com.testerum.file_service.file.util.isCreateResource
import com.testerum.file_service.file.util.isRelocateResource
import com.testerum.model.exception.ValidationException
import com.testerum.model.exception.model.ValidationModel
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.resources.ResourceContext
import com.testerum.model.resources.ResourceType
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import java.nio.file.Path as JavaPath

class ResourceFileService {

    companion object {
        private val LOG = LoggerFactory.getLogger(ResourceFileService::class.java)

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

    fun getResourceByPathAndType(path: Path,
                                 resourceType: ResourceType,
                                 resourcesDir: JavaPath): ResourceContext? {
        val resourceJavaPath = getJavaPath(path, resourceType, resourcesDir)

        val escapedPath = path.escape()
        val fileContent = resourceJavaPath.getContent()

        return ResourceContext(
                path = escapedPath,
                oldPath = escapedPath,
                body = fileContent
        )
    }

    fun getResourceAtPath(path: Path,
                          resourcesDir: JavaPath): ResourceContext? {
        val resourceType = ResourceType.getByFileExtension(path)
                ?: throw java.lang.IllegalArgumentException("unrecognized resource file extension [${path.fileExtension}]]")

        val resourceJavaPath = getJavaPath(path, resourceType, resourcesDir)

        val escapedPath = path.escape()
        val fileContent = resourceJavaPath.getContent()

        return ResourceContext(
                path = escapedPath,
                oldPath = escapedPath,
                body = fileContent
        )
    }

    fun save(resourceContext: ResourceContext,
             resourcesDir: JavaPath): ResourceContext {
        val resourceType = ResourceType.getByFileExtension(resourceContext.path)
                ?: throw java.lang.IllegalArgumentException("unrecognized resource file extension [${resourceContext.path.fileExtension}]]")

        validateResource(resourceType, resourceContext, resourcesDir)

        val resourceWithTransformedBody: ResourceContext = resourceContext.copy(
                body = FileArgTransformer.jsonToFileFormat(resourceContext.body, resourceType.javaType)
        )

        val oldEscapedPath = resourceWithTransformedBody.oldPath?.escape()
        val newEscapedPath = resourceWithTransformedBody.path.escape()

        val oldResourceFile: JavaPath? = oldEscapedPath?.let {
            getJavaPath(oldEscapedPath, resourceType, resourcesDir)
        }
        val newResourceFile = getJavaPath(newEscapedPath, resourceType, resourcesDir)

        // handle rename
        if (oldResourceFile != null && newResourceFile != oldResourceFile) {
            if (newResourceFile.exists) {
                throw ValidationException(
                        ValidationModel(
                                globalValidationMessage = "the resource at path [$newEscapedPath] already exists"
                        )
                )
            }

            Files.move(oldResourceFile, newResourceFile)
        }

        // write the new resource file
        newResourceFile.parent?.createDirectories()

        Files.write(
                newResourceFile,
                resourceWithTransformedBody.body.toByteArray(Charsets.UTF_8),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )

        return resourceContext.copy(
                path = newEscapedPath,
                oldPath = newEscapedPath
        )
    }

    private fun validateResource(resourceType: ResourceType, resourceContext: ResourceContext, resourcesDir: JavaPath) {
        validatePathConflict(resourceType, resourceContext, resourcesDir)
        validateRdbmsConnectionConfig(resourceType, resourceContext)
    }

    private fun validatePathConflict(resourceType: ResourceType, resourceContext: ResourceContext, resourcesDir: JavaPath) {
        if (!resourceContext.isCreateResource() && !resourceContext.isRelocateResource()) {
            return
        }
        if (resourceContext.isRelocateResource() && resourceContext.path.toString().equals(resourceContext.oldPath.toString(), true)) {
            return
        }

        if (existResourceAtPath(resourceContext.path, resourceType, resourcesDir)) {
            throw ValidationException().addFieldValidationError(
                    "name",
                    "a_resource_with_the_same_name_already_exist"
            )
        }
    }

    private fun validateRdbmsConnectionConfig(resourceType: ResourceType, resourceContext: ResourceContext) {
        if (resourceType != ResourceType.RDBMS_CONNECTION) {
            return
        }

        try {
            OBJECT_MAPPER.readValue<RdbmsConnectionConfig>(resourceContext.body)
        } catch (e: Exception) {
            throw ValidationException("The following text is not a valid Rdbms Connection Config Json: [${resourceContext.body}].")
        }
    }

    private fun existResourceAtPath(path: Path,
                                    resourceType: ResourceType,
                                    resourcesDir: JavaPath): Boolean {
        val javaPath = getJavaPath(path, resourceType, resourcesDir)

        return javaPath.exists && javaPath.isRegularFile
    }

    fun delete(path: Path, resourcesDir: JavaPath) {
        val resourceType = ResourceType.getByFileExtension(path)
                ?: throw java.lang.IllegalArgumentException("unrecognized resource file extension [${path.fileExtension}]]")

        val escapedPath = path.escape()

        val resourceFile = getJavaPath(escapedPath, resourceType, resourcesDir)

        resourceFile.deleteIfExists()
    }

    fun getPathsOfSharedResources(resourceType: ResourceType,
                                  resourcesDir: JavaPath): List<Path> {
        val fileTypeRootPath = resourcesDir.resolve(resourceType.relativeRootDir)
        if (fileTypeRootPath.doesNotExist) {
            return emptyList()
        }

        val javaPaths = fileTypeRootPath.walkAndCollect {
            it.isRegularFile && it.hasExtension(resourceType.fileExtension)
        }

        return javaPaths.map {
            Path.createInstance(
                    fileTypeRootPath.relativize(it).toString()
            )
        }
    }

    fun renameDirectory(renamePath: RenamePath,
                        resourcesDir: JavaPath): Path {
        val javaPathToRename = resourcesDir.resolve(
                renamePath.path.escape().toString()
        )

        if (javaPathToRename.doesNotExist) {
            LOG.warn("ignoring attempt to rename directory that doesn't exist [$javaPathToRename]")
            return renamePath.path
        }

        val escapedNewName = renamePath.newName.escapeFileOrDirectoryName()

        val javaNewPath = javaPathToRename.resolveSibling(escapedNewName)

        if (javaPathToRename.differsOnlyInCasingFrom(javaNewPath)) {
            val tempPath = javaPathToRename.resolveSibling(UUID.randomUUID().toString())
            Files.move(javaPathToRename, tempPath)
            Files.move(tempPath, javaNewPath)
        } else {
            Files.move(javaPathToRename, javaNewPath)
        }

        val javaNewRelativePath = resourcesDir.toAbsolutePath().normalize()
                .relativize(javaNewPath.toAbsolutePath().normalize())

        return Path.createInstance(
                javaNewRelativePath.toString()
        )
    }

    fun deleteDirectory(path: Path,
                        resourcesDir: JavaPath) {
        val javaDirToDelete = resourcesDir.resolve(
                path.escape().toString()
        )

        javaDirToDelete.deleteIfExists()
    }

    // todo: remove duplication with ComposedStepFileService.moveComposedStepDirectoryOrFile
    fun moveDirectoryOrFile(copyPath: CopyPath,
                            resourceType: ResourceType,
                            resourcesDir: JavaPath): Path {
        val fileTypeRootPath = resourcesDir.resolve(resourceType.relativeRootDir)

        val escapedSourceFile = copyPath.copyPath.escape()
        val escapedDestinationPath = copyPath.destinationPath.escape()

        val escapedDestinationFile = if (escapedDestinationPath.isFile()) {
            escapedDestinationPath
        } else {
            escapedDestinationPath.copy(
                    fileName = escapedSourceFile.fileName,
                    fileExtension = escapedSourceFile.fileExtension
            )
        }

        val sourceJavaFile = fileTypeRootPath.resolve(
                escapedSourceFile.toString()
        )
        val destinationJavaFile = fileTypeRootPath.resolve(
                escapedDestinationFile.toString()
        )

        if (destinationJavaFile.exists) {
            throw ValidationException(
                    ValidationModel(
                            globalValidationMessage = "the file at path [$escapedDestinationFile] already exists"
                    )
            )
        }

        destinationJavaFile.parent?.createDirectories()

        Files.move(sourceJavaFile, destinationJavaFile)

        return escapedDestinationFile

    }

    private fun getJavaPath(path: Path, resourceType: ResourceType, resourcesDir: JavaPath): JavaPath {
        return resourcesDir.resolve(resourceType.relativeRootDir)
                .resolve(path.escape().toString())
                .toAbsolutePath()
                .normalize()
    }

}
