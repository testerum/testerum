package selenium_steps_support.service.webdriver_factory.util

import org.apache.commons.lang3.SystemUtils

object OsUtils {

    val is64BitArchitecture: Boolean
        get() {
            val osArchitecture = osArchitecture

            return (osArchitecture == "amd64"
                    || osArchitecture == "x86_64"
                    || osArchitecture == "i686")
        }

    val is32BitArchitecture: Boolean
        get() {
            val osArchitecture = osArchitecture

            return osArchitecture == "x86" || osArchitecture == "i386"
        }

    val isWindows: Boolean
        get() = SystemUtils.IS_OS_WINDOWS

    val isMac: Boolean
        get() = SystemUtils.IS_OS_MAC

    val isLinux: Boolean
        get() = SystemUtils.IS_OS_LINUX

    val osArchitecture: String
        get() = SystemUtils.OS_ARCH

}
