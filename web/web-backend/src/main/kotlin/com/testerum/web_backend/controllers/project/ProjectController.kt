package com.testerum.web_backend.controllers.project

import com.testerum.model.home.Home
import com.testerum.model.home.Project
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.license.AuthRequest
import com.testerum.model.license.AuthResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projects")
open class ProjectController() {

    @RequestMapping(method = [RequestMethod.GET])
    @ResponseBody
    fun getProjects(): List<Project> {

        val recentProjects = listOf(
                Project("Superman", Path.createInstance("c:/projects/superman")),
                Project("Batman", Path.createInstance("c:/projects/batman")),
                Project("SampleProject", Path.createInstance(".Testerum/samplePorject"))
        )

        return recentProjects;
    }

    @RequestMapping (method = [RequestMethod.POST])
    @ResponseBody
    fun createProject(@RequestBody project: Project): Project {
        return project;
    }
}
