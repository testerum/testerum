package net.qutester.service.resources

import com.fasterxml.jackson.databind.ObjectMapper
import net.qutester.model.infrastructure.path.CopyPath
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.infrastructure.path.RenamePath
import net.qutester.model.repository.enums.FileType
import net.qutester.model.resources.ResourceContext
import net.qutester.service.resources.handler.ResourceHandler
import net.qutester.service.resources.util.isRelocateResource
import net.qutester.service.resources.validators.ResourceValidator
import net.testerum.db_file.FileRepositoryService
import net.testerum.db_file.model.KnownPath
import net.testerum.db_file.model.mapToRepositoryFileChange

class ResourcesService(val validators: List<ResourceValidator>,
                       val handlers: Map<FileType, ResourceHandler>,
                       val fileRepositoryService: FileRepositoryService,
                       val objectMapper: ObjectMapper) {

    fun save(resourceContext: ResourceContext): ResourceContext {

        val fileType: FileType? = FileType.getResourceTypeByExtension(resourceContext.path)

        if (fileType == null) {
            throw RuntimeException("Unrecognized resource type [${resourceContext.path.fileExtension}]]")
        }

        validateResource(fileType, resourceContext)

        val handledResource = handleResource(fileType, resourceContext)

        val save = fileRepositoryService.save(handledResource.mapToRepositoryFileChange(fileType))
        if (resourceContext.isRelocateResource()) {
            //TODO: remove references from tests and steps resources to this resource
            fileRepositoryService.delete(
                    KnownPath(resourceContext.oldPath!!, fileType)
            )
        }
        return save.mapToResource()
    }

    private fun handleResource(fileType: FileType, resourceContext: ResourceContext): ResourceContext {
        val resourceHandler = handlers[fileType]

        if (resourceHandler == null) {
            return resourceContext
        }
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
        val fileType: FileType? = FileType.getResourceTypeByExtension(path)

        if (fileType == null) {
            throw RuntimeException("Unrecognized resource type [${path.fileExtension}]]")
        }

        fileRepositoryService.delete(
                KnownPath(path, fileType)
        )
    }

    fun getByPath(path: Path): ResourceContext? {
        val resourceTypeByExtension = FileType.getResourceTypeByExtension(path)
                ?: throw RuntimeException("Unknown FileType based on the path extension [${path}]")
        return fileRepositoryService.getByPath(KnownPath(path, resourceTypeByExtension))?.mapToResource()
    }

    fun isThereAnyResourceAtPath(path: Path): Boolean {
        val resourceTypeByExtension = FileType.getResourceTypeByExtension(path)
                ?: throw RuntimeException("Unknown FileType based on the path extension [${path}]")
        return fileRepositoryService.existResourceAtPath(KnownPath(path, resourceTypeByExtension))
    }

    fun <T> getResourceBodyAs (resourcePath: Path, resourceBodyClassType: Class<T>): T {
        val resource = getByPath(resourcePath)

        if (resource == null) {
            throw RuntimeException ("No resource on the path ["+resourcePath.toString()+"] was found")
        }

        return objectMapper.readValue(resource.body, resourceBodyClassType)
    }

    fun renameDirectory(renamePath: RenamePath): Path {

        val fileType: FileType? = FileType.getResourceTypeByExtension(renamePath.path)

        if (fileType == null) {
            throw RuntimeException("Unrecognized resource type [${renamePath.path.fileExtension}]]")
        }

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