package com.testerum.web_backend.controllers.home

import com.testerum.model.home.Home
import com.testerum.web_backend.services.project.ProjectFrontendService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/home")
open class HomeController(private val projectFrontendService: ProjectFrontendService) {

    @RequestMapping(method = [RequestMethod.GET])
    @ResponseBody
    fun getHomePageModel(): Home {
        return Home(
                "We work to make your life better",
                "v0.7.3",
                projectFrontendService.getProjects()
        )
    }
}
