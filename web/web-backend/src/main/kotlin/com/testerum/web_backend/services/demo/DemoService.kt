package com.testerum.web_backend.services.demo

import com.testerum.settings.TesterumDirs
import org.apache.commons.io.FileUtils

class DemoService (private val testerumDirs: TesterumDirs) {

    fun copyDemoFilesFromInstallDirToUserSettingsDir() {

        FileUtils.copyDirectory(
                testerumDirs.getDemoDir().resolve("tests").toFile(),
                testerumDirs.getDemoTestsDir().toFile()
        )
    }

}
