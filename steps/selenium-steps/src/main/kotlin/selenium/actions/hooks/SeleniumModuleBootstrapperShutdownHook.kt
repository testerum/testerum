package selenium.actions.hooks

import com.testerum_api.testerum_steps_api.annotations.hooks.AfterAllTests
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator

class SeleniumModuleBootstrapperShutdownHook {

    @AfterAllTests
    fun shutdownContext() {
        SeleniumModuleServiceLocator.bootstrapper.context.shutdown()
    }

}
