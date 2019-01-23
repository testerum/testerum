package com.testerum.web_backend.controllers.home

import com.testerum.model.home.Home
import com.testerum.web_backend.services.home.HomeFrontendService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/home")
open class HomeController(private val homeFrontendService: HomeFrontendService) {

    @RequestMapping(method = [RequestMethod.GET])
    @ResponseBody
    fun getHomePageModel(): Home {
        return homeFrontendService.getHomePageModel()
    }

}
