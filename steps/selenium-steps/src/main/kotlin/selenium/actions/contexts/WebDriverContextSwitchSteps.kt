package selenium.actions.contexts

import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.When
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.frame_switchers.FrameSwitcher
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import com.testerum.common_text_matchers.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverContextSwitchSteps {

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    // todo: documentation

    @When(
            value = "I switch the context to the frame <<frameLocator>>",
            description = "Switches the context to the frame/iframe identified by the given frame locator.\n" +
                    "The context determines to what window or frame do element selectors apply." +
                    " For example, when trying to identify an element using something like ``css=.username``, the context determines to what window or frame this applies.\n" +
                    "After this step, the context will be switched to the frame/iframe identified by the given frame locator."
    )
    fun switchToFrame(
            @Param(
                    description = SeleniumSharedDescriptions.FRAME_LOCATOR_DESCRIPTION
            )
            frameLocator: String
    ) {
        webDriverManager.switchCurrentWebDriver { driver ->
            return@switchCurrentWebDriver FrameSwitcher.switchToFrame(driver, frameLocator)
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I switch the context to the top frame",
            description = "Switches the context to the main page.\n" +
                    "The context determines to what window or frame do element selectors apply." +
                    " For example, when trying to identify an element using something like ``css=.username``, the context determines to what window or frame this applies.\n" +
                    "If the current page doesn't use frames or iframes, this step doesn't have any effect." +
                    " Otherwise, if the context was on a particular frame, the context will be switched to the main document."
    )
    fun switchToTopFrame() {
        webDriverManager.switchCurrentWebDriverToTop()
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I switch the context to the window with the title <<textMatchExpression>>",
            description = "Switches the context to the window whose title matches the given expression.\n" +
                    "The context determines to what window or frame do element selectors apply." +
                    " For example, when trying to identify an element using something like ``css=.username``, the context determines to what window or frame this applies.\n" +
                    "This step is particularly useful when interacting with web apps that use popups."
    )
    fun switchToWindow(
        @Param(
                description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
        )
        textMatchExpression: String
    ) {
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
