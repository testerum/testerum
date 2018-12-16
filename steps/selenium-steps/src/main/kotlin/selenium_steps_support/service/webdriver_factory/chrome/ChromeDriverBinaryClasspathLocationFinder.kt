package selenium_steps_support.service.webdriver_factory.chrome

import org.apache.commons.lang3.SystemUtils
import selenium_steps_support.service.webdriver_factory.util.OsUtils

object ChromeDriverBinaryClasspathLocationFinder {

    private val BASE_PATH = "webdrivers/chrome"

    fun getClasspathLocationForCurrentOperatingSystem(): String {
        val isWindows = OsUtils.isWindows
        val isLinux = OsUtils.isLinux
        val isMac = OsUtils.isMac

        val is64BitArchitecture = OsUtils.is64BitArchitecture

        if (isWindows) {
            return BASE_PATH + "/windows/chromedriver_win32_v2.45.exe"
        } else if (isLinux) {
            if (is64BitArchitecture) {
                return BASE_PATH + "/linux/chromedriver_linux64_v2.45"
            } else {
                throw IllegalArgumentException(
                        "unsupported Linux architecture [" + OsUtils.osArchitecture + "]"
                )
            }
        } else if (isMac) {
            if (is64BitArchitecture) {
                return BASE_PATH + "/mac/chromedriver_mac64_v2.45"
            } else {
                throw IllegalArgumentException(
                        "unsupported Mac architecture [" + OsUtils.osArchitecture + "]"
                )
            }
        } else {
            throw IllegalArgumentException(
                    "unsupported operating system" +
                    "name=[" + SystemUtils.OS_NAME + "]" +
                    ", version=[" + SystemUtils.OS_VERSION + "]" +
                    ", architecture=[" + OsUtils.osArchitecture + "]"
            )
        }
    }

}
