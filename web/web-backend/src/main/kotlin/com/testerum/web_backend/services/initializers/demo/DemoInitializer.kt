package com.testerum.web_backend.services.initializers.demo

import com.testerum.web_backend.services.demo.DemoService
import kotlin.concurrent.thread

class DemoInitializer(private val demoService: DemoService) {

    fun initialize() {
        thread(start = true) {
            demoService.copyDemoFilesFromInstallDirToUserSettingsDir();
        }
    }
}
