package com.testerum.web_backend.controllers.filesystem

import com.testerum.model.config.dir_tree.CreateFileSystemDirectoryRequest
import com.testerum.model.config.dir_tree.FileSystemDirectory
import com.testerum.model.infrastructure.path.PathInfo
import com.testerum.web_backend.services.filesystem.FileSystemFrontendService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/file_system")
class FileSystemController(private val fileSystemFrontendService: FileSystemFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = ["/directory_tree"])
    @ResponseBody
    fun getDirectoryTree(@RequestParam(value = "path") pathAsString:String,
                         @RequestParam(value = "showFiles") showFiles:Boolean): FileSystemDirectory {
        return fileSystemFrontendService.getDirectoryTree(pathAsString, showFiles)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/create_directory"])
    @ResponseBody
    fun createDirectory(@RequestBody createRequest: CreateFileSystemDirectoryRequest): FileSystemDirectory {
        return fileSystemFrontendService.createDirectory(createRequest)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/path/info"])
    @ResponseBody
    fun getPathInfo(@RequestParam(value = "path") pathAsString:String): PathInfo {
        return fileSystemFrontendService.getPathInfo(pathAsString)
    }
}
