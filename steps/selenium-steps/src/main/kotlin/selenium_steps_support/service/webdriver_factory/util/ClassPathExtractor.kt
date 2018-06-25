package selenium_steps_support.service.webdriver_factory.util

import org.apache.commons.io.IOUtils
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path

class ClassPathExtractor constructor(private val classLoader: ClassLoader = ClassPathExtractor::class.java.classLoader /*Thread.currentThread().contextClassLoader*/) {

    fun extractToTemp( classpathLocation: String,
                       prefix: String): Path {
        if (!classpathResourceExists(classpathLocation, classLoader)) {
            throw IllegalArgumentException(
                    "cannot find [" + classpathLocation + "]" +
                    " in the class path" +
                    " using classLoader [" + classLoader + "]"
            )
        }

        try {
            return tryToExtractToTemp(classpathLocation, prefix)
        } catch (e: Exception) {
            throw ClasspathExtractorException(
                    "failed to extract" +
                            " class path resource [" + classpathLocation + "]" +
                            " (loader [" + classLoader + "])" +
                            " to the temp directory",
                    e
            )
        }
    }

    private fun tryToExtractToTemp(classpathLocation: String,
                                   prefix: String): Path {
        val tempFile = Files.createTempFile(prefix, "")
        tempFile.toFile().deleteOnExit()

        classLoader.getResourceAsStream(classpathLocation).use { input ->
            FileOutputStream(tempFile.toFile()).use {
                output -> IOUtils.copy(input, output)
            }
        }

        return tempFile
    }

    private fun classpathResourceExists(executableClasspathLocation: String,
                                        classLoader: ClassLoader): Boolean {
        return classLoader.getResource(executableClasspathLocation) != null
    }
}
