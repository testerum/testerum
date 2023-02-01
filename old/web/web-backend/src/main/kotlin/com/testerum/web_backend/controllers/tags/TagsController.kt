package com.testerum.web_backend.controllers.tags

import com.testerum.web_backend.services.tags.TagsFrontendService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tags")
class TagsController(private val tagsFrontendService: TagsFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    fun getAllTags(): List<String> {
        return tagsFrontendService.getAllTags()
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/manual"])
    fun getManualTags(): List<String> {
        return tagsFrontendService.getManualTestsTags()
    }

}
