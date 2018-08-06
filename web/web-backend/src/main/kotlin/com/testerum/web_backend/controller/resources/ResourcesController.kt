package com.testerum.web_backend.controller.resources

import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.repository.enums.FileType
import com.testerum.model.resources.ResourceContext
import com.testerum.service.resources.ResourcesService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/resources")
class ResourcesController(private val resourcesService: ResourcesService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getByPath(@RequestParam("path") path: String): ResourceContext? {
        return resourcesService.getByPath(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun save(@RequestBody resourceContext: ResourceContext): ResourceContext {
        return resourcesService.save(resourceContext)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""])
    @ResponseBody
    fun delete(@RequestParam("path") path: String) {
        resourcesService.delete(path)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/shared/paths/{resourceType:.+}"])
    @ResponseBody
    fun getResourcesPath(@PathVariable(value = "resourceType") resourceTypeAsString: String): ResponseEntity<List<String>> {
        val resourceType = FileType.getByFileExtension(resourceTypeAsString)
                ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val paths: List<Path> = resourcesService.getPathOfSharedResources(resourceType)

        val response: MutableList<String> = mutableListOf()
        for (path in paths) {
            response.add(path.toString())
        }
        return ResponseEntity(response, HttpStatus.OK)
    }

    @RequestMapping(method = [RequestMethod.PUT], path = ["/directory"])
    @ResponseBody
    fun renameDirectory(@RequestBody renamePath: RenamePath): Path {
        return resourcesService.renameDirectory(renamePath)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["/directory"])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        resourcesService.deleteDirectory(pathAsString)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/directory/move"])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        resourcesService.moveDirectoryOrFile(copyPath)
    }

}