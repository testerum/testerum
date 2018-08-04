package net.qutester.controller.resources

import net.qutester.model.infrastructure.path.CopyPath
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.infrastructure.path.RenamePath
import net.qutester.model.repository.enums.FileType
import net.qutester.model.resources.ResourceContext
import net.qutester.service.resources.ResourcesService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/resources")
class ResourcesController(private val resourcesService: ResourcesService) {
    private val LOG = LoggerFactory.getLogger(ResourcesController::class.java)

    @RequestMapping(method = [RequestMethod.POST])
    @ResponseBody
    fun save(@RequestBody resourceContext: ResourceContext): ResourceContext {
        return resourcesService.save(resourceContext)
    }

    @RequestMapping(path = ["/shared/paths/{resourceType:.+}"], method = [RequestMethod.GET])
    @ResponseBody
    fun getResourcesPath(@PathVariable(value = "resourceType") resourceTypeAsString: String): ResponseEntity<List<String>> {
        val resourceType = FileType.getByFileExtension(resourceTypeAsString)
        if (resourceType == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val paths: List<Path> = resourcesService.getPathOfSharedResources(resourceType)

        val response: MutableList<String> = mutableListOf()
        for (path in paths) {
            response.add(path.toString())
        }
        return ResponseEntity(response, HttpStatus.OK)
    }

    @RequestMapping(method = [RequestMethod.DELETE])
    @ResponseBody
    fun delete(@RequestParam("path") path: String) {
        resourcesService.delete(path)
    }

    @RequestMapping(method = [RequestMethod.GET])
    @ResponseBody
    fun getByPath(@RequestParam("path") path: String): ResourceContext? {
        return resourcesService.getByPath(Path.createInstance(path))
    }

    @RequestMapping(path = ["/directory"], method = [RequestMethod.PUT])
    @ResponseBody
    fun renameDirectory(@RequestBody renamePath: RenamePath): Path {
        return resourcesService.renameDirectory(renamePath)
    }

    @RequestMapping(path = ["/directory"], method = [RequestMethod.DELETE])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        resourcesService.deleteDirectory(pathAsString)
    }

    @RequestMapping(path = ["/directory/move"], method = [RequestMethod.POST])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        resourcesService.moveDirectoryOrFile(copyPath)
    }
}