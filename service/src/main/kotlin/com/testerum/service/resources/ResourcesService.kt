package com.testerum.service.resources

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.service.file_repository.FileRepositoryService
import com.testerum.service.file_repository.model.KnownPath
import com.testerum.service.file_repository.model.RepositoryFile
import com.testerum.service.file_repository.model.RepositoryFileChange
import com.testerum.service.file_repository.model.mapToRepositoryFileChange
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.repository.enums.FileType
import com.testerum.model.resources.ResourceContext
import com.testerum.service.mapper.file_arg_transformer.FileArgTransformer
import com.testerum.service.resources.handler.ResourceHandler
import com.testerum.service.resources.util.isRelocateResource
import com.testerum.service.resources.validators.ResourceValidator

class ResourcesService(val validators: List<ResourceValidator>,
                       val handlers: Map<FileType, ResourceHandler>,
                       val fileRepositoryService: FileRepositoryService,
                       val objectMapper: ObjectMapper) {

    fun save(resourceContext: ResourceContext): ResourceContext {
        val fileType: FileType = FileType.getResourceTypeByExtension(resourceContext.path)
                ?: throw RuntimeException("Unrecognized resource type [${resourceContext.path.fileExtension}]]")

        validateResource(fileType, resourceContext)

        val handledResource = handleResource(fileType, resourceContext)

        val resourceWithTransformedBody: ResourceContext = handledResource.copy(
                body = FileArgTransformer.jsonToFileFormat(handledResource.body, fileType.resourceJavaType)
        )

        val repositoryFileChange: RepositoryFileChange = resourceWithTransformedBody.mapToRepositoryFileChange(fileType)
        val savedResource = fileRepositoryService.save(repositoryFileChange)
        if (resourceContext.isRelocateResource()) {
            //TODO: remove references from tests and steps resources to this resource
            fileRepositoryService.delete(
                    KnownPath(resourceContext.oldPath!!, fileType)
            )
        }


        val savedResourceWithTransformedBody: RepositoryFile = savedResource.copy(
                body = FileArgTransformer.fileFormatToJson(savedResource.body, fileType.resourceJavaType).orEmpty()
        )

        return savedResourceWithTransformedBody.mapToResource()
    }

    private fun handleResource(fileType: FileType, resourceContext: ResourceContext): ResourceContext {
        val resourceHandler = handlers[fileType]
                ?: return resourceContext

        return resourceHandler.handle(resourceContext)
    }

    private fun validateResource(fileType: FileType, resourceContext: ResourceContext) {
        for (validator in validators) {
            if (validator.canValidate(fileType)) {
                validator.validate(resourceContext, fileType)
            }
        }
    }

    fun getPathOfSharedResources(fileType: FileType): List<Path> {
        return fileRepositoryService.getAllPathsOfResourcesByType(fileType)
    }

    fun delete(pathAsString: String) {
        val path = Path.createInstance(pathAsString)
        val fileType: FileType = FileType.getResourceTypeByExtension(path)
                ?: throw RuntimeException("Unrecognized resource type [${path.fileExtension}]]")

        fileRepositoryService.delete(
                KnownPath(path, fileType)
        )
    }

    fun getByPath(path: Path): ResourceContext? {
        val fileType = FileType.getResourceTypeByExtension(path)
                ?: throw RuntimeException("Unknown FileType based on the path extension [$path]")
        val resourceFile: RepositoryFile = fileRepositoryService.getByPath(KnownPath(path, fileType))
                ?: return null

        val resourceFileWithTransformedBody = resourceFile.copy(
                body = FileArgTransformer.fileFormatToJson(resourceFile.body, fileType.resourceJavaType).orEmpty()
        )

        return resourceFileWithTransformedBody.mapToResource()
    }

    fun <T> getResourceBodyAs (resourcePath: Path, resourceBodyClassType: Class<T>): T {
        val resource = getByPath(resourcePath)
                ?: throw RuntimeException ("No resource on the path ["+resourcePath.toString()+"] was found")

        return objectMapper.readValue(resource.body, resourceBodyClassType)
    }

    fun renameDirectory(renamePath: RenamePath): Path {

        val fileType: FileType = FileType.getResourceTypeByExtension(renamePath.path)
                ?: throw RuntimeException("Unrecognized resource type [${renamePath.path.fileExtension}]]")

        return fileRepositoryService.renameDirectory(
                knownPath = KnownPath(renamePath.path, fileType),
                newName = renamePath.newName
        )
    }

    fun deleteDirectory(pathAsString: String) {
        fileRepositoryService.delete(
                KnownPath(pathAsString, FileType.RESOURCE)
        )
    }


    fun moveDirectoryOrFile(copyPath: CopyPath): Path {
        var fileType: FileType = FileType.RESOURCE
        if(copyPath.copyPath.isFile()) {
            fileType = FileType.getResourceTypeByExtension(copyPath.copyPath)?:
                    throw RuntimeException("Unrecognized resource type [${copyPath.copyPath.fileExtension}]]")

        }

        return fileRepositoryService.moveDirectoryOrFile(
                KnownPath(copyPath.copyPath, fileType),
                KnownPath(copyPath.destinationPath, fileType)
        )
    }

}
