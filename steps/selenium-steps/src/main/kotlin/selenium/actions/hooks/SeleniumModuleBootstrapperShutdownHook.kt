package selenium.actions.hooks

import com.testerum_api.testerum_steps_api.annotations.hooks.AfterAllTests
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator

class SeleniumModuleBootstrapperShutdownHook {

    @com.testerum_api.testerum_steps_api.annotations.hooks.AfterAllTests
    fun shutdownContext() {
        SeleniumModuleServiceLocator.bootstrapper.context.shutdown()
    }

}
