package appium.android

import appium.AppiumServiceLocator
import appium.model.AndroidElementLocatorType
import appium.model.AppiumDriverType
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.AndroidElement
import org.openqa.selenium.By

object AndroidStepsUtils {
    val driver = AppiumServiceLocator.driverManager.driver(AppiumDriverType.ANDROID) as AndroidDriver<AndroidElement>

    fun findElement(
        elementLocatorType: AndroidElementLocatorType,
        elementLocator: String
    ): AndroidElement {
        return when (elementLocatorType) {
            AndroidElementLocatorType.Id -> driver.findElementById(elementLocator)
            AndroidElementLocatorType.ClassName -> driver.findElementByClassName(elementLocator)
            AndroidElementLocatorType.CssSelector -> driver.findElementByCssSelector(elementLocator)
            AndroidElementLocatorType.LinkText -> driver.findElementByLinkText(elementLocator)
            AndroidElementLocatorType.Name -> driver.findElementByName(elementLocator)
            AndroidElementLocatorType.PartialLinkText -> driver.findElementByPartialLinkText(elementLocator)
            AndroidElementLocatorType.TagName -> driver.findElementByTagName(elementLocator)
            AndroidElementLocatorType.XPath -> driver.findElementByXPath(elementLocator)
        } ?: throw AssertionError("the field identified by [$elementLocatorType]  [$elementLocator] should be present on the page, but is not")
    }

    fun getElementBy(
        elementLocatorType: AndroidElementLocatorType,
        elementLocator: String
    ): By {
        return when (elementLocatorType) {
            AndroidElementLocatorType.Id -> By.id(elementLocator)
            AndroidElementLocatorType.ClassName -> By.className(elementLocator)
            AndroidElementLocatorType.CssSelector -> By.cssSelector(elementLocator)
            AndroidElementLocatorType.LinkText -> By.linkText(elementLocator)
            AndroidElementLocatorType.Name -> By.name(elementLocator)
            AndroidElementLocatorType.PartialLinkText -> By.linkText(elementLocator)
            AndroidElementLocatorType.TagName -> By.tagName(elementLocator)
            AndroidElementLocatorType.XPath -> By.xpath(elementLocator)
        } ?: throw AssertionError("the field identified by [$elementLocatorType]  [$elementLocator] should be present on the page, but is not")
    }
}
