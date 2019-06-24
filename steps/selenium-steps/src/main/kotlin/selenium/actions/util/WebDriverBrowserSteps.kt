package selenium.actions.util

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.services.TesterumServiceLocator
import org.openqa.selenium.Dimension
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager



class WebDriverBrowserSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @Given(
            value = "the browser width is <<browserWidth>> and height is <<browserHeight>>",
            description = "Resize the browser window to the specified width and height in pixels.\n" +
                    "If no values are provided, the default width is 1024px and default height is 700px."
    )
    fun click(
            @Param(
                    description = "This parameter needs to be a number and represents the browser window width in pixels.\n" +
                            "The default value is 1024"
            )
            browserWidth: Int?,
            @Param(
                    description = "This parameter needs to be a number and represents the browser window height in pixels.\n" +
                            "The default value is 700"
            )
            browserHeight: Int?
    ) {
        val resolvedBrowserWidth = if(browserWidth != null && 0 < browserWidth) browserWidth else 1024;
        val resolvedBrowserHeight = if(browserHeight != null && 0 < browserHeight) browserHeight else 1024;
        logger.info(
                "resizing browser window\n" +
                "--------\n" +
                "browserWidth : $resolvedBrowserWidth\n" +
                "browserHeight : $resolvedBrowserHeight\n" +
                "\n"
        )

        val browserDimension = Dimension(resolvedBrowserWidth, resolvedBrowserHeight)

        webDriverManager.executeWebDriverStep { driver ->
            driver.manage().window().setSize(browserDimension)
        }
    }
}
