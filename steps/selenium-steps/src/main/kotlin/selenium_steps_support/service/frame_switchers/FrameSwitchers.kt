package selenium_steps_support.service.frame_switchers

import com.google.common.base.Splitter
import org.openqa.selenium.WebDriver
import selenium_steps_support.service.elem_locators.ElementLocatorService
import java.util.regex.Pattern

object FrameSwitchers {

    private val SPLITTER = Splitter.on(Pattern.compile("\n|\r\n"))
            .trimResults()
            .omitEmptyStrings()

    fun switchToFrame(topDriver: WebDriver,
                      frameLocator: String): WebDriver {
        val frameLocatorParts = SPLITTER.split(frameLocator).toList()

        var driver = topDriver
        for (frameLocatorPart in frameLocatorParts) {
            val element = ElementLocatorService.locateElement(driver, frameLocatorPart)
                    ?: throw RuntimeException("failed to find element [$frameLocatorPart")
            driver = driver.switchTo().frame(element)
        }

        return driver
    }

}
