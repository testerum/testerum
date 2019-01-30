package com.testerum.web_backend.controllers.project

import com.testerum.model.home.Project
import com.testerum.web_backend.controllers.project.model.CreateProjectRequest
import com.testerum.web_backend.services.project.ProjectFrontendService
import org.springframework.web.bind.annotation.*

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

    @RequestMapping (method = [RequestMethod.DELETE], path = ["/recent"], params = ["path"])
    @ResponseBody
    fun deleteRecentProject(@RequestParam(value = "path") path: String) {
        projectFrontendService.deleteRecentProject(path)
    }

}
