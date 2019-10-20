package com.testerum.web_backend.services.initializers.demo

import com.testerum.web_backend.services.demo.DemoService

class DemoInitializer(private val demoService: DemoService) {

    fun initialize() {
        demoService.copyDemoFilesFromInstallDirToUserSettingsDir();
    }
}
