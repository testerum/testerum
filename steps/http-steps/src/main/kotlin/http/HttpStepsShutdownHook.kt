package http

import com.testerum.api.annotations.hooks.AfterAllTests
import http_support.module_bootstrapper.HttpStepsModuleServiceLocator

class HttpStepsShutdownHook {

    @AfterAllTests
    fun shutdown() {
        HttpStepsModuleServiceLocator.bootstrapper.context.shutdown()
    }

}