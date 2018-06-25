package net.qutester.controller.config

import net.qutester.model.config.dir_tree.FileSystemDirectory
import net.qutester.model.infrastructure.path.Path
import net.qutester.service.config.FileSystemService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/file_system")
class FileSystemController(val fileSystemService: FileSystemService) {

    @RequestMapping(path = arrayOf("/directory_tree"), method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getDirectoryTree(@RequestParam(value = "path") pathAsString:String): FileSystemDirectory {
        var path: Path = Path.createEmptyInstance();
        if (pathAsString.isNotEmpty()) {
            path = Path.createInstance(pathAsString)
        }
        return fileSystemService.getDirectoryTree(path)
    }
}
