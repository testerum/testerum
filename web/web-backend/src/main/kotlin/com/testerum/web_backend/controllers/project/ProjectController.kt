package com.testerum.web_backend.controllers.project

import com.testerum.model.home.Project
import com.testerum.web_backend.services.project.ProjectFrontendService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
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
    fun createProject(@RequestBody project: Project): Project {
        // todo: implement this
        return project
    }

}
