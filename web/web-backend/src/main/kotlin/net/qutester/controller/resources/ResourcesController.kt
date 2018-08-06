package net.qutester.controller.resources

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

    @RequestMapping(method = [RequestMethod.POST])
    @ResponseBody
    fun save(@RequestBody resourceContext: ResourceContext): ResourceContext {
        return resourcesService.save(resourceContext)
    }

    @RequestMapping(path = ["/shared/paths/{resourceType:.+}"], method = [RequestMethod.GET])
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