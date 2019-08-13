package selenium.actions.page

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import org.openqa.selenium.Dimension
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverPageSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @Given(
            value = "the page at url <<url>> is open",
            description = "Navigates to the given URL."
    )
    fun givenThePageAtUrlIsOpen(url: String) {
        logger.info(
                "opening page\n" +
                        "------------\n" +
                        "url : $url\n" +
                        "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().to(url)
        }
    }

    @When(
            value = "I navigate to url <<url>>"
    )
    fun whenINavigateToUrl(url: String) {
        logger.info(
                "opening page\n" +
                        "------------\n" +
                        "url : $url\n" +
                        "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().to(url)
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Given(
            value = "the window width is <<windowWidth>> and height is <<windowHeight>>",
            description = "Resize the browser window to the specified width and height in pixels.\n" +
                    "If no values are provided, the default width is 1024px and default height is 700px."
    )
    fun resizeWindow(
            @Param(
                    description = "This parameter needs to be a number and represents the browser window width in pixels.\n" +
                            "The default value is 1024"
            )
            windowWidth: Int?,
            @Param(
                    description = "This parameter needs to be a number and represents the browser window height in pixels.\n" +
                            "The default value is 700"
            )
            windowHeight: Int?
    ) {
        val resolvedBrowserWidth = if (windowWidth != null && 0 < windowWidth) windowWidth else 1024;
        val resolvedBrowserHeight = if (windowHeight != null && 0 < windowHeight) windowHeight else 1024;
        logger.info(
                "resizing browser window\n" +
                        "-----------------------\n" +
                        "windowWidth : $resolvedBrowserWidth\n" +
                        "windowHeight : $resolvedBrowserHeight\n" +
                        "\n"
        )

        val browserDimension = Dimension(resolvedBrowserWidth, resolvedBrowserHeight)

        webDriverManager.executeWebDriverStep { driver ->
            driver.manage().window().setSize(browserDimension)
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I close the current window",
            description = "Close the current window, quitting the browser if it's the last window currently open.\n" +
                    "There is no need to close the initial window, IDE will re-use it; closing it may cause a performance penalty on the test.\n"
    )
    fun closeWindow() {
        logger.info(
                "close current window\n"
        )

        webDriverManager.closeWindow()
    }


//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I navigate to the previous page",
            description = "Goes backward in the browser's history."
    )
    fun navigateToThePreviousPage() {
        logger.info("navigating to the previous page\n\n")

        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().back()
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I refresh the current page",
            description = "Reloads the current page, just like pressing the Reload button in the browser."
    )
    fun refreshTheCurrentPage() {
        logger.info("refreshing the current page\n\n")

        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().refresh()
        }
    }
//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I navigate to the next page",
            description = "Goes forward in the browser's history."
    )
    fun navigateToTheNextPage() {
        logger.info("navigating to the next page\n\n")

        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().forward()
        }
    }

//----------------------------------------------------------------------------------------------------------------------

}
