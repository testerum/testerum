package selenium.actions.contexts

import com.testerum.api.annotations.steps.When
import selenium_steps_support.service.frame_switchers.FrameSwitchers
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverContextSwitchSteps {

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    // todo: documentation

    @When(
            value = "I switch the context to the frame <<frameLocator>>"
    )
    fun switchToFrame(frameLocator: String) {
        webDriverManager.switchCurrentWebDriver { topWebDriver ->
            return@switchCurrentWebDriver FrameSwitchers.switchToFrame(topWebDriver, frameLocator)
        }
    }

    @When(
            value = "I switch the context to the top frame"
    )
    fun switchToTopFrame() {
        webDriverManager.switchCurrentWebDriverToTop()
    }

}
