package selenium.actions

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import org.openqa.selenium.WebElement
import org.springframework.beans.factory.annotation.Autowired
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverFormSteps @Autowired constructor(private val webDriverManager: WebDriverManager) {

    @When("I type <<text>> into the field <<elementLocator>>")
    fun sendKeys(@Param(required = false) text: String?, elementLocator: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            // todo: parse "text" and send org.openqa.selenium.Keys if needed

            field.clear()
            field.sendKeys(text.orEmpty())
        }
    }

    @When("I submit the form containing the field identified by the element locator <<elementLocator>>")
    fun submitTheForm(elementLocator: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            field.submit()
        }
    }

}