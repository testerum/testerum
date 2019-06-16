package selenium_steps_support.utils

import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.isNotADirectory
import java.nio.file.Path
import java.nio.file.Paths

object SeleniumStepsDirs {

    private fun getInstallDir(): Path = run {
        val packageDirectoryProperty = System.getProperty("testerum.packageDirectory")
                ?: throw IllegalArgumentException("missing required [testerum.packageDirectory] system property")

        val path: Path = Paths.get(packageDirectoryProperty)
        val absolutePath: Path = path.toAbsolutePath().normalize()

        if (absolutePath.doesNotExist) {
            throw IllegalArgumentException("package directory [$path] (resolved as absolutePath) does not exist")
        }
        if (absolutePath.isNotADirectory) {
            throw IllegalArgumentException("package directory [$path] (resolved as absolutePath) is not a directory")
        }

        return@run absolutePath
    }

    fun getSeleniumDriversDir(): Path = getInstallDir().resolve("selenium-drivers")

}
