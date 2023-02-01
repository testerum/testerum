package http

import com.testerum_api.testerum_steps_api.annotations.hooks.AfterAllTests
import http_support.module_di.HttpStepsModuleServiceLocator

class HttpStepsShutdownHook {

    @AfterAllTests
    fun shutdown() {
        HttpStepsModuleServiceLocator.bootstrapper.context.shutdown()
    }

}
