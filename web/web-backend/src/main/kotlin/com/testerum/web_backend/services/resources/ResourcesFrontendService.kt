package com.testerum.web_backend.services.resources

import com.testerum.file_service.caches.resolved.resolvers.file_arg_transformer.FileArgTransformer
import com.testerum.file_service.file.ResourceFileService
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.resources.ResourceContext
import com.testerum.model.resources.ResourceType
import com.testerum.settings.keys.SystemSettingKeys
import com.testerum.web_backend.services.dirs.FrontendDirs
import java.nio.file.Path as JavaPath

class ResourcesFrontendService(private val frontendDirs: FrontendDirs,
                               private val resourceFileService: ResourceFileService) {

    fun getResourceAtPath(path: Path): ResourceContext? {
        val resourcesDir = getResourcesDir()

        val resource = resourceFileService.getResourceAtPath(path, resourcesDir)
                ?: return null

        val resourceType = ResourceType.getByFileExtension(path)
                ?: throw RuntimeException("unknown ResourceType based on the path extension [$path]")
        val transformedBody = FileArgTransformer.fileFormatToJson(resource.body, resourceType.javaType).orEmpty()

        return resource.copy(
                body = transformedBody
        )
    }

    fun save(resourceContext: ResourceContext): ResourceContext {
        val resourcesDir = getResourcesDir()

        return resourceFileService.save(resourceContext, resourcesDir)
    }

    fun delete(path: Path) {
        val resourcesDir = getResourcesDir()

        resourceFileService.delete(path, resourcesDir)
    }

    fun getPathsOfSharedResources(resourceType: ResourceType): List<Path> {
        val resourcesDir = getResourcesDir()

        return resourceFileService.getPathsOfSharedResources(resourceType, resourcesDir)
    }

    fun renameDirectory(renamePath: RenamePath): Path {
        val resourcesDir = getResourcesDir()

        return resourceFileService.renameDirectory(renamePath, resourcesDir)
    }

    fun deleteDirectory(path: Path) {
        val resourcesDir = getResourcesDir()

        return resourceFileService.deleteDirectory(path, resourcesDir)
    }

    fun moveDirectoryOrFile(copyPath: CopyPath): Path {
        val resourcesDir = getResourcesDir()
        val resourceType = ResourceType.getByFileExtension(copyPath.copyPath)
                ?: throw RuntimeException("unknown ResourceType based on the path extension [${copyPath.copyPath}]")


        return resourceFileService.moveDirectoryOrFile(copyPath, resourceType, resourcesDir)
    }

    private fun getResourcesDir(): JavaPath {
        val repositoryDir = frontendDirs.getRepositoryDir()
                ?: throw IllegalStateException("the setting [${SystemSettingKeys.REPOSITORY_DIR}] is not set")

        return frontendDirs.getResourcesDir(repositoryDir)
    }

}
