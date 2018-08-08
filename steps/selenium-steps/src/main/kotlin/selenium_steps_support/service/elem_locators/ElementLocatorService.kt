package selenium_steps_support.service.elem_locators

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import selenium_steps_support.ByJs

object ElementLocatorService {

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

    private val validPrefixes: List<String> = prefixToLocatorFactoryMap.keys.map { it + "=" }

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
        val indexOfEquals: Int = elementLocator.indexOf("=")

        if (indexOfEquals == -1) {
            throw IllegalArgumentException("invalid element locator; it must start with one of ${validPrefixes}")
        }

        val locatorType = elementLocator.substring(0, indexOfEquals)
        val locatorFactory: ((driver: WebDriver, elementLocator: String) -> By) = prefixToLocatorFactoryMap[locatorType]
                ?: throw IllegalArgumentException("invalid element locator; it must start with one of ${validPrefixes}, but got $locatorType")

        val elementLocatorWithoutPrefix = if (indexOfEquals == elementLocator.length - 1) {
            ""
        } else {
            elementLocator.substring(indexOfEquals + 1)
        }

        return locatorFactory.invoke(driver, elementLocatorWithoutPrefix)
    }

}