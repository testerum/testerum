package com.testerum.web_backend.controllers.features

import com.testerum.model.infrastructure.path.Path
import com.testerum.web_backend.services.features.FeaturesFrontendService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/attachments")
class FeatureAttachmentsController(private val featuresFrontendService: FeaturesFrontendService) {


    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getAttachmentFileContent(@RequestParam(value = "path") pathAsString: String,
                                 @RequestParam(value = "thumbnail", required = false) thumbnail: Boolean,
                                 response: HttpServletResponse) {
        featuresFrontendService.writeAttachmentFileContentToResponse(
                Path.createInstance(pathAsString),
                thumbnail,
                response
        )
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") pathAsString: String) {
        featuresFrontendService.deleteAttachment(
                Path.createInstance(pathAsString)
        )
    }

}
