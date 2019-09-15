package com.testerum.web_backend.services.resources

import com.testerum.file_service.caches.resolved.resolvers.file_arg_transformer.FileArgTransformer
import com.testerum.file_service.file.ResourceFileService
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.resources.ResourceContext
import com.testerum.model.resources.ResourceType
import com.testerum.web_backend.services.project.WebProjectManager
import java.nio.file.Path as JavaPath

class ResourcesFrontendService(private val webProjectManager: WebProjectManager,
                               private val resourceFileService: ResourceFileService) {

    private fun getResourcesDir(): JavaPath = webProjectManager.getProjectServices().dirs().getResourcesDir()

    fun getResourceAtPath(path: Path): ResourceContext? {
        val resourcesDir = getResourcesDir()

        val resource = resourceFileService.getResourceAtPath(path, resourcesDir)
                ?: return null

        val resourceType = ResourceType.getByFileExtension(path)
                ?: throw RuntimeException("unknown ResourceType based on the path extension [$path]")
        val transformedBody = FileArgTransformer.fileFormatToJson(resource.body, resourceType.typeMeta).orEmpty()

        return resource.copy(
                body = transformedBody
        )
    }

    fun save(resourceContext: ResourceContext): ResourceContext {
        val resourcesDir = getResourcesDir()

        val result = resourceFileService.save(resourceContext, resourcesDir)

        reinitializeCaches()

        return result
    }

    fun delete(path: Path) {
        val resourcesDir = getResourcesDir()

        resourceFileService.delete(path, resourcesDir)

        reinitializeCaches()
    }

    fun getPathsOfSharedResources(resourceType: ResourceType): List<Path> {
        val resourcesDir = getResourcesDir()

        return resourceFileService.getPathsOfSharedResources(resourceType, resourcesDir)
    }

    fun renameDirectory(renamePath: RenamePath): Path {
        val resourcesDir = getResourcesDir()

        val result = resourceFileService.renameDirectory(renamePath, resourcesDir)

        reinitializeCaches()

        return result
    }

    fun deleteDirectory(path: Path) {
        val resourcesDir = getResourcesDir()

        resourceFileService.deleteDirectory(path, resourcesDir)

        reinitializeCaches()
    }

    fun moveDirectoryOrFile(copyPath: CopyPath): Path {
        val resourcesDir = getResourcesDir()
        val resourceType = ResourceType.getByFileExtension(copyPath.copyPath)
                ?: throw RuntimeException("unknown ResourceType based on the path extension [${copyPath.copyPath}]")


        val result = resourceFileService.moveDirectoryOrFile(copyPath, resourceType, resourcesDir)

        reinitializeCaches()

        return result
    }

    private fun reinitializeCaches() {
        // re-loading steps & tests to make sure tests are resolved properly
        // to optimize, we could re-load only the affected tests and/or steps
        webProjectManager.getProjectServices().reinitializeStepsCache()
        webProjectManager.getProjectServices().reinitializeTestsCache()
    }

}
