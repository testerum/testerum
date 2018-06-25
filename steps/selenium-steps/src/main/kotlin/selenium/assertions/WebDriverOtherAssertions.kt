package selenium.assertions

import com.testerum.api.annotations.steps.Then
import org.springframework.beans.factory.annotation.Autowired
import selenium_steps_support.service.text_match.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverOtherAssertions @Autowired constructor(private val webDriverManager: WebDriverManager) {

    @Then("the title of the current page should be <<textMatchExpression>>")
    fun assertCurrentPageTitleShouldBe(textMatchExpression: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val actualText: String = driver.title

            if (!TextMatcherService.matches(textMatchExpression, actualText)) {
                throw AssertionError("the title of the current page does not match: expected [$textMatchExpression] but was [$actualText]")
            }
        }
    }

    @Then("the title of the current page should not be <<textMatchExpression>>")
    fun assertCurrentPageTitleShouldNotBe(textMatchExpression: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val actualText: String = driver.title

            if (TextMatcherService.matches(textMatchExpression, actualText)) {
                throw AssertionError("the title of the current page matches: expected not to match [$textMatchExpression] but was [$actualText]")
            }
        }
    }

    @Then("the url of the current page should be <<textMatchExpression>>")
    fun assertCurrentUrlShouldBe(textMatchExpression: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val actualText: String = driver.currentUrl

            if (!TextMatcherService.matches(textMatchExpression, actualText)) {
                throw AssertionError("the url of the current page does not match: expected [$textMatchExpression] but was [$actualText]")
            }
        }
    }

    @Then("the url of the current page should not be <<textMatchExpression>>")
    fun assertCurrentUrlShouldNotBe(textMatchExpression: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val actualText: String = driver.currentUrl

            if (TextMatcherService.matches(textMatchExpression, actualText)) {
                throw AssertionError("the url of the current page matches: expected not to match [$textMatchExpression] but was [$actualText]")
            }
        }
    }

}
