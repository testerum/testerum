package com.testerum.api.test_context.settings

import com.testerum.api.services.TesterumService
import java.nio.file.Path as JavaPath

interface RunnerTesterumDirs : TesterumService {

    fun getJdbcDriversDir(): JavaPath

}
