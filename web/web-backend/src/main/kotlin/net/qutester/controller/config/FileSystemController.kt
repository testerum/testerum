package net.qutester.controller.config

import com.testerum.model.config.dir_tree.FileSystemDirectory
import com.testerum.model.infrastructure.path.Path
import com.testerum.service.config.FileSystemService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/file_system")
class FileSystemController(val fileSystemService: FileSystemService) {

    @RequestMapping(method = [RequestMethod.GET], path = ["/directory_tree"])
    @ResponseBody
    fun getDirectoryTree(@RequestParam(value = "path") pathAsString:String): FileSystemDirectory {
        var path: Path = Path.EMPTY
        if (pathAsString.isNotEmpty()) {
            path = Path.createInstance(pathAsString)
        }
        return fileSystemService.getDirectoryTree(path)
    }

}
