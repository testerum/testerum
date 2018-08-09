package selenium_steps_support.service.module_di

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class SeleniumModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    val webDriverManager = WebDriverManager(
            settingsManager = TesterumServiceLocator.getSettingsManager()
    )

}