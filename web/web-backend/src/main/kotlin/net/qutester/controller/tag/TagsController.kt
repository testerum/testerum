package net.qutester.controller.tag

import net.qutester.service.tags.TagsService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tags")
class TagsController(private val tagsService: TagsService) {

    @RequestMapping(method = [RequestMethod.GET])
    fun get(): List<String> {
        return tagsService.getAllTags()
    }

}