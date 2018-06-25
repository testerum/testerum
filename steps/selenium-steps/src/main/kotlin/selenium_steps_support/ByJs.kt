package selenium_steps_support

import org.openqa.selenium.*

class ByJs(private val driver: WebDriver,
           private val jsCode: String) : By() {

    override fun findElements(context: SearchContext): List<WebElement> {
        val element = (driver as JavascriptExecutor).executeScript(jsCode) as WebElement

        return listOf(element)
    }
}