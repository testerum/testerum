package selenium_steps_support.service.module_di

import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_service.file.SeleniumDriversFileService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class SeleniumModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    val webDriverManager = WebDriverManager(
            runnerSettingsManager = TesterumServiceLocator.getSettingsManager(),
            seleniumDriversFileService = SeleniumDriversFileService()
    )

}
