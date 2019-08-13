package selenium.actions.contexts

import com.testerum.api.annotations.steps.When
import selenium_steps_support.service.frame_switchers.FrameSwitcher
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.text_match.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverContextSwitchSteps {

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    // todo: documentation

    @When(
            value = "I switch the context to the frame <<frameLocator>>"
    )
    fun switchToFrame(frameLocator: String) {
        webDriverManager.switchCurrentWebDriver { topWebDriver ->
            return@switchCurrentWebDriver FrameSwitcher.switchToFrame(topWebDriver, frameLocator)
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I switch the context to the top frame"
    )
    fun switchToTopFrame() {
        webDriverManager.switchCurrentWebDriverToTop()
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I switch the context to the window with the title <<textMatchExpression>>"
    )
    fun switchToWindow(textMatchExpression: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val originalWindowHandle = driver.windowHandle

            for (windowHandle in driver.windowHandles) {
                driver.switchTo().window(windowHandle)

                if (TextMatcherService.matches(textMatchExpression, driver.title)) {
                    return@executeWebDriverStep
                }
            }

            // nothing matched => restore original context & throw error
            driver.switchTo().window(originalWindowHandle)

            throw RuntimeException("could not find any window that matches the text expression [$textMatchExpression]")
        }
    }

}
