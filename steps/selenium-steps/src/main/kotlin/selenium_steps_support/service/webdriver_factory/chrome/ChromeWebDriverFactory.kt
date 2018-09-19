package selenium_steps_support.service.webdriver_factory.chrome

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.slf4j.LoggerFactory
import selenium_steps_support.service.webdriver_factory.util.ClassPathExtractor
import selenium_steps_support.service.webdriver_factory.util.OsUtils
import java.nio.file.Path as JavaPath

object ChromeWebDriverFactory {

    private val LOG = LoggerFactory.getLogger(ChromeWebDriverFactory::class.java)

    fun createWebDriver(): WebDriver {
        // todo: replace extracting from classpath with driver management in the UI
        // todo: this is nasty: it's a global variable preventing us from using different drivers (e.g. different Chrome versions) at the same time ==> find a better way)
        val chromeBinaryTempPath = getChromeDriverBinaryTempPath()
        System.setProperty("webdriver.chrome.driver", chromeBinaryTempPath.toAbsolutePath().toString())

        // augmenting allows us to take screenshots

        return ChromeDriver()
    }

    private fun getChromeDriverBinaryTempPath(): JavaPath {
        val classpathLocation = ChromeDriverBinaryClasspathLocationFinder.getClasspathLocationForCurrentOperatingSystem()

        val classPathExtractor = ClassPathExtractor()

        LOG.debug("Based on the current OS, Chrome driver binary is the classpath at [{}]; extracting to temp dir", classpathLocation)
        val fileToExecute = classPathExtractor.extractToTemp(classpathLocation, "chromedriver-")

        if (OsUtils.isLinux || OsUtils.isMac) {
            LOG.debug("Chrome driver binary extracted to [{}]; marking it as executable", fileToExecute.toAbsolutePath())

            val success = fileToExecute.toFile().setExecutable(true)
            if (!success) {
                throw IllegalArgumentException("failed to make extracted file executable")
            }
        }

        return fileToExecute
    }
}
