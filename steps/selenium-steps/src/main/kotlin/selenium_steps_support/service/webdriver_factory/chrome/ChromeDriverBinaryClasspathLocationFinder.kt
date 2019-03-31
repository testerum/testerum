package selenium_steps_support.service.webdriver_factory.chrome

import com.testerum.common_jdk.OsUtils
import org.apache.commons.lang3.SystemUtils

object ChromeDriverBinaryClasspathLocationFinder {

    private val BASE_PATH = "webdrivers/chrome"

    fun getClasspathLocationForCurrentOperatingSystem(): String {
        val isWindows = OsUtils.IS_WINDOWS
        val isLinux = OsUtils.IS_LINUX
        val isMac = OsUtils.IS_MAC

        val is64BitArchitecture = OsUtils.IS_64BIT_ARCHITECTURE

        if (isWindows) {
            return BASE_PATH + "/windows/chromedriver_win32_v2.45.exe"
        } else if (isLinux) {
            if (is64BitArchitecture) {
                return BASE_PATH + "/linux/chromedriver_linux64_v2.45"
            } else {
                throw IllegalArgumentException(
                        "unsupported Linux architecture [" + OsUtils.OS_ARCHITECTURE + "]"
                )
            }
        } else if (isMac) {
            if (is64BitArchitecture) {
                return BASE_PATH + "/mac/chromedriver_mac64_v2.45"
            } else {
                throw IllegalArgumentException(
                        "unsupported Mac architecture [" + OsUtils.OS_ARCHITECTURE + "]"
                )
            }
        } else {
            throw IllegalArgumentException(
                    "unsupported operating system" +
                    "name=[" + SystemUtils.OS_NAME + "]" +
                    ", version=[" + SystemUtils.OS_VERSION + "]" +
                    ", architecture=[" + OsUtils.OS_ARCHITECTURE + "]"
            )
        }
    }

}
