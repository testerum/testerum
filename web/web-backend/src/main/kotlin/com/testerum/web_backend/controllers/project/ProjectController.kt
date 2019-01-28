package com.testerum.web_backend.controllers.project

import com.testerum.model.home.Project
import com.testerum.web_backend.controllers.project.model.CreateProjectRequest
import com.testerum.web_backend.services.project.ProjectFrontendService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

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
    fun getComposedStepAtPath(@RequestParam(value = "path") path: String): Project {
        // todo: implement this
        return Project("happy", path, LocalDateTime.now())
    }
}
