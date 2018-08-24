package com.testerum.web_backend.controller.config

import com.testerum.model.config.dir_tree.FileSystemDirectory
import com.testerum.service.settings.FileSystemService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/file_system")
class FileSystemController(val fileSystemService: FileSystemService) {

    @RequestMapping(method = [RequestMethod.GET], path = ["/directory_tree"])
    @ResponseBody
    fun getDirectoryTree(@RequestParam(value = "path") pathAsString:String): FileSystemDirectory {
        return fileSystemService.getDirectoryTree(pathAsString)
    }

}
