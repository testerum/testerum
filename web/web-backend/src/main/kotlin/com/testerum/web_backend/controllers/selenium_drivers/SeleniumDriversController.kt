package com.testerum.web_backend.controllers.selenium_drivers

import com.testerum.api.test_context.settings.model.SeleniumBrowserType
import com.testerum.model.selenium.SeleniumDriverInfo
import com.testerum.model.selenium.SeleniumDriversByBrowser
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/selenium-drivers")
class SeleniumDriversController {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getDriversInfo(): SeleniumDriversByBrowser {
        return SeleniumDriversByBrowser(
                mapOf(
                        SeleniumBrowserType.CHROME to listOf(
                                SeleniumDriverInfo("75.0.3770.8", listOf("75")),
                                SeleniumDriverInfo("74.0.3729.6", listOf("74")),
                                SeleniumDriverInfo("73.0.3683.68", listOf("73")),
                                SeleniumDriverInfo("2.46", listOf("71", "72")),
                                SeleniumDriverInfo("2.45", listOf("70"))
                        ),

                        SeleniumBrowserType.FIREFOX to listOf(
                                SeleniumDriverInfo("0.24.0", listOf("57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67")),
                                SeleniumDriverInfo("0.20.1", listOf("55", "56")),
                                SeleniumDriverInfo("0.18.0", listOf("53", "54")),

                                SeleniumDriverInfo("0.17.0", listOf("52"))
                        )
                )
        )
    }

}
