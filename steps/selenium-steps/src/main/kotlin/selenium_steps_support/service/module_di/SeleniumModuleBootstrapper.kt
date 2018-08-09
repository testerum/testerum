package selenium_steps_support.service.module_di

import com.testerum.common_di.ModuleFactoryContext
import selenium_steps_support.service.module_di.SeleniumModuleFactory

class SeleniumModuleBootstrapper {

    val context = ModuleFactoryContext()

    val seleniumModuleFactory = SeleniumModuleFactory(context)

}