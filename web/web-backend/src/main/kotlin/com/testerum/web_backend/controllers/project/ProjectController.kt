package com.testerum.web_backend.controllers.project

import com.testerum.model.home.Project
import com.testerum.model.home.CreateProjectRequest
import com.testerum.web_backend.services.project.ProjectFrontendService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/projects")
open class ProjectController(private val projectFrontendService: ProjectFrontendService) {

    @RequestMapping(method = [RequestMethod.GET])
    @ResponseBody
    fun getProjects(): List<Project> {
        return projectFrontendService.getProjects()
    }

    @RequestMapping (method = [RequestMethod.POST])
    @ResponseBody
    fun createProject(@RequestBody createProjectRequest: CreateProjectRequest): Project {
        return projectFrontendService.createProject(createProjectRequest)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/open"], params = ["path"])
    @ResponseBody
    fun openProject(@RequestParam(value = "path") path: String): Project {
        return projectFrontendService.openProject(path)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/reload"], params = ["path"])
    @ResponseBody
    fun reloadProject(@RequestParam(value = "path") path: String) {
        projectFrontendService.reloadProject(path)
    }

    @RequestMapping (method = [RequestMethod.DELETE], path = ["/recent"], params = ["path"])
    @ResponseBody
    fun deleteRecentProject(@RequestParam(value = "path") path: String) {
        projectFrontendService.deleteRecentProject(path)
    }


    @RequestMapping (method = [RequestMethod.PUT], path = ["/rename"])
    @ResponseBody
    fun renameProject(@RequestBody project: Project): Project {
        return projectFrontendService.renameProject(project)
    }
}
