package selenium_steps_support.service.elem_locators

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import selenium_steps_support.ByJs

object ElementLocatorService {

    private data class ElementLocator(val type: String, val expression: String)

    // todo: make this map pluggable
    private val prefixToLocatorFactoryMap: Map<String, (driver: WebDriver, elementLocatorWithoutPrefix: String) -> By> = mapOf(
            "id"               to { _     , elementLocatorWithoutPrefix -> By.id(elementLocatorWithoutPrefix) },
            "name"             to { _     , elementLocatorWithoutPrefix -> By.name(elementLocatorWithoutPrefix) },
            "css"              to { _     , elementLocatorWithoutPrefix -> By.cssSelector(elementLocatorWithoutPrefix) },
            "linkText"         to { _     , elementLocatorWithoutPrefix -> By.linkText(elementLocatorWithoutPrefix) },
            "linkTextContains" to { _     , elementLocatorWithoutPrefix -> By.partialLinkText(elementLocatorWithoutPrefix) },
            "xpath"            to { _     , elementLocatorWithoutPrefix -> By.xpath(elementLocatorWithoutPrefix) },
            "js"               to { driver, elementLocatorWithoutPrefix -> ByJs(driver, elementLocatorWithoutPrefix) }
            )

    private val DEFAULT_LOCATOR_TYPE = "css"

    private val validLocatorTypes: Collection<String> = prefixToLocatorFactoryMap.keys

    /**
     * @return the first matching element on the current page
     * @throws ElementNotFoundException if no matching elements are found
     * @throws ElementLocatorServiceException if an error occurred while trying to find the element
     */
    fun locateRequiredElement(driver: WebDriver, elementLocator: String): WebElement {
        try {
            val selector: By = getSelector(elementLocator, driver)

            return driver.findElement(selector)
        } catch (e: org.openqa.selenium.NoSuchElementException) {
            throw ElementNotFoundException("could not find the element [$elementLocator]", e)
        } catch (e: Exception) {
            throw ElementLocatorServiceException("an error occurred while trying to find the element [$elementLocator]", e)
        }
    }

    /**
     * @return the first matching element on the current page, or null if no element could be found
     * @throws ElementLocatorServiceException if an error occurred while trying to find the element
     */
    fun locateElement(driver: WebDriver, elementLocator: String): WebElement? {
        try {
            return locateRequiredElement(driver, elementLocator)
        } catch (e: ElementNotFoundException) {
            return null
        }
    }

    private fun getSelector(elementLocator: String, driver: WebDriver): By {
        val locator = parseLocator(elementLocator)

        val locatorFactory: ((driver: WebDriver, elementLocator: String) -> By) = prefixToLocatorFactoryMap[locator.type]
                ?: throw IllegalArgumentException("invalid element locator type; it should be one of $validLocatorTypes, but got [${locator.type}]")

        return locatorFactory.invoke(driver, locator.expression)
    }

    private fun parseLocator(elementLocator: String): ElementLocator {
        val indexOfEquals: Int = elementLocator.indexOf("=")

        return if (indexOfEquals == -1) {
            ElementLocator(
                    type = DEFAULT_LOCATOR_TYPE,
                    expression = elementLocator
            )
        } else {
            ElementLocator(
                    type = elementLocator.substring(0, indexOfEquals),
                    expression = elementLocator.substring(indexOfEquals + 1)
            )
        }
    }

}
