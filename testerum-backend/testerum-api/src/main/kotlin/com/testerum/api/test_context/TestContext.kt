package com.testerum.api.test_context

import com.testerum.api.services.TesterumService

interface TestContext : TesterumService {

    val testStatus: ExecutionStatus
    val stepsClassLoader: ClassLoader

}
