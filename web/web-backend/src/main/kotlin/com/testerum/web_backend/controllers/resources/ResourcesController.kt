package com.testerum.web_backend.controllers.resources

import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.resources.ResourceContext
import com.testerum.model.resources.ResourceType
import com.testerum.web_backend.services.resources.ResourcesFrontendService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/resources")
class ResourcesController(private val resourcesFrontendService: ResourcesFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getResourceAtPath(@RequestParam("path") path: String): ResourceContext? {
        return resourcesFrontendService.getResourceAtPath(
                Path.createInstance(path)
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun save(@RequestBody resourceContext: ResourceContext): ResourceContext {
        return resourcesFrontendService.save(resourceContext)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""])
    @ResponseBody
    fun delete(@RequestParam("path") pathAsString: String) {
        resourcesFrontendService.delete(
                Path.createInstance(pathAsString)
        )
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/shared/paths/{resourceFileExtension:.+}"])
    @ResponseBody
    fun getResourcePaths(@PathVariable(value = "resourceFileExtension") resourceFileExtension: String): ResponseEntity<List<String>> {
        val resourceType = ResourceType.getByFileExtension(resourceFileExtension)
                ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val paths: List<Path> = resourcesFrontendService.getPathsOfSharedResources(resourceType)

        val response = paths.map { it.toString() }

        return ResponseEntity(response, HttpStatus.OK)
    }

    @RequestMapping(method = [RequestMethod.PUT], path = ["/directory"])
    @ResponseBody
    fun renameDirectory(@RequestBody renamePath: RenamePath): Path {
        return resourcesFrontendService.renameDirectory(renamePath)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["/directory"])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        resourcesFrontendService.deleteDirectory(
                Path.createInstance(pathAsString)
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/directory/move"])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        resourcesFrontendService.moveDirectoryOrFile(copyPath)
    }

}
