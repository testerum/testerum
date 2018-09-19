package com.testerum.runner_cmdline.classloader

import com.testerum.api.test_context.settings.model.resolvedValueAsPath
import com.testerum.runner_cmdline.RunnerApplication
import com.testerum.settings.SettingsManager
import com.testerum.settings.getRequiredSetting
import com.testerum.settings.keys.SystemSettingKeys
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import java.util.stream.Collectors
import java.nio.file.Path as JavaPath

class RunnerClassloaderFactory(private val settingsManager: SettingsManager) {

    // todo: how to reduce duplication between this class and StepLibraryPersistentCacheManger?
    fun createStepsClassLoader(): ClassLoader {
        val jars: List<JavaPath> = getStepJarFiles()

        val jarUrls: Array<URL> = jars
                .filter { !it.toString().contains("testerum-api-") } // todo: remove this hack - run the scanner in a separate process and scan the classpath instead
                .map { it.toUri().toURL() }
                .toTypedArray()

        return URLClassLoader(jarUrls, RunnerApplication::class.java.classLoader)
    }

    // todo: how to reduce duplication between this class and BasicStepScanner?
    private fun getStepJarFiles(): List<JavaPath> {
        // todo: user step directory
        val basicStepsDirectory: JavaPath = settingsManager.getRequiredSetting(SystemSettingKeys.BUILT_IN_BASIC_STEPS_DIR).resolvedValueAsPath

        val isJarFile = BiPredicate { file: JavaPath, _: BasicFileAttributes ->
            Files.isRegularFile(file) && file.toString().endsWith(".jar")
        }

        Files.find(basicStepsDirectory, 1, isJarFile).use { stream ->
            return stream.collect(Collectors.toList())
        }
    }

}
