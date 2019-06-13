package com.testerum.web_backend.controllers.selenium_drivers

import com.testerum.model.selenium.SeleniumDriversByBrowser
import com.testerum.web_backend.services.selenium_drivers.SeleniumDriversFrontendService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/selenium-drivers")
class SeleniumDriversController(private val seleniumDriversFrontendService: SeleniumDriversFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getDriversInfo(): SeleniumDriversByBrowser {
        return seleniumDriversFrontendService.getDriversInfo()
    }

}
