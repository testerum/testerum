package com.testerum.web_backend.controllers.version_info

import com.testerum.web_backend.services.version_info.VersionInfoFrontendService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/version-info")
class VersionInfoController(private val versionInfoFrontendService: VersionInfoFrontendService) {

    @RequestMapping (method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getVersionInfo(): Map<String, String> {
        return versionInfoFrontendService.getVersionProperties()
    }

}
