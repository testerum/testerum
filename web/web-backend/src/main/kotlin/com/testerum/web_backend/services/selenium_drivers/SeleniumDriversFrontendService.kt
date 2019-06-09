package com.testerum.web_backend.services.selenium_drivers

import com.testerum.file_service.file.SeleniumDriversFileService
import com.testerum.model.selenium.SeleniumDriversByBrowser
import com.testerum.web_backend.services.dirs.FrontendDirs

class SeleniumDriversFrontendService(private val seleniumDriversFileService: SeleniumDriversFileService,
                                     private val frontendDirs: FrontendDirs) {

    fun getDriversInfo(): SeleniumDriversByBrowser {
        val seleniumDriversDir = frontendDirs.getSeleniumDriversDir()

        return seleniumDriversFileService.getDriversInfo(seleniumDriversDir)
    }

}
