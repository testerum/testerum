package com.testerum.web_backend.controllers.project

import com.testerum.model.home.Project
import com.testerum.web_backend.services.project.ProjectFrontendService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
    fun createProject(@RequestBody project: Project): Project {
        // todo: implement this
        return project
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/open"], params = ["path"])
    @ResponseBody
    fun getComposedStepAtPath(@RequestParam(value = "path") path: String): Project {
        // todo: implement this
        return Project("happy", path, LocalDateTime.now())
    }
}
