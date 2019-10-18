package com.testerum.web_backend.controllers.demo

import com.testerum.model.home.Project
import com.testerum.web_backend.services.demo.DemoService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/demo")
open class DemoController(private val demoService: DemoService) {

    @RequestMapping(method = [RequestMethod.POST], path = ["/start"])
    @ResponseBody
    fun startDemoApp(): Project {
        demoService.startDemoApp();

        return demoService.getDemoProject()
    }
}
