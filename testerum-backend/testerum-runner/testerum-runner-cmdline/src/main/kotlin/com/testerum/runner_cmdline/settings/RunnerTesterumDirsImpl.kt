package com.testerum.runner_cmdline.settings

import com.testerum_api.testerum_steps_api.test_context.settings.RunnerTesterumDirs
import com.testerum.settings.TesterumDirs
import java.nio.file.Path as JavaPath

class RunnerTesterumDirsImpl(private val testerumDirs: TesterumDirs) : RunnerTesterumDirs {

    override fun getJdbcDriversDir(): JavaPath = testerumDirs.getJdbcDriversDir()

}
