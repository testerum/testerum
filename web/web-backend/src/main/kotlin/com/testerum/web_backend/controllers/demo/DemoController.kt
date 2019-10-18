package com.testerum.web_backend.controllers.demo

import com.testerum.model.home.Project
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/demo")
open class DemoController() {

    @RequestMapping(method = [RequestMethod.POST], path = ["/start"])
    @ResponseBody
    fun startDemoApp(): Project {
        return Project(
                "Testerum Demo", "to be defined", LocalDateTime.now()
        )
    }
}
