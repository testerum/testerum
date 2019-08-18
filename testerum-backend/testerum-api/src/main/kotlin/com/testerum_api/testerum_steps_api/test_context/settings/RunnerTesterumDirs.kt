package com.testerum_api.testerum_steps_api.test_context.settings

import com.testerum_api.testerum_steps_api.services.TesterumService
import java.nio.file.Path as JavaPath

interface RunnerTesterumDirs : TesterumService {

    fun getJdbcDriversDir(): JavaPath

}
