package net.qutester.service.scanner

import com.testerum.api.test_context.settings.model.Setting
import com.testerum.scanner.step_lib_scanner.StepLibraryCacheManger
import com.testerum.scanner.step_lib_scanner.model.ScannerBasicStepScanResult
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.settings.SystemSettings
import com.testerum.settings.private_api.SettingsManagerImpl
import net.qutester.model.step.BasicStepDef
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import java.util.stream.Collectors

class ScannerService(private val settingsManager: SettingsManagerImpl,
                     private val stepLibraryCacheManger: StepLibraryCacheManger) {

    companion object {
        private val cacheFile: Path = SettingsManagerImpl.settingsDirectory.resolve("cache/basic-steps-cache.json")
    }

    fun init() {
        // start loading in the background (by calling the getter) & also register settings
        Thread(Runnable {
            for (library in scanResult.libraries) {
                val settings: List<Setting> = library.settings

                settingsManager.registerSettings(settings)
            }
        }).start()
    }

    fun getBasicSteps(): List<BasicStepDef> = scanResult.libraries.flatMap { it.steps }

    fun getHooks(): List<HookDef> = scanResult.libraries.flatMap { it.hooks }

    private val scanResult: ScannerBasicStepScanResult by lazy {
        stepLibraryCacheManger.scan(
                getJarFiles(),
                cacheFile
        )
    }

    private fun getJarFiles(): List<Path> {
        val basicStepsDirectory: Path = getBasicStepsDirectory()

        val isJarFile = BiPredicate { file: Path, _: BasicFileAttributes ->
            Files.isRegularFile(file) && file.toString().endsWith(".jar")
        }

        Files.find(basicStepsDirectory, 1, isJarFile).use { stream ->
            return stream.collect(Collectors.toList())
        }
    }

    private fun getBasicStepsDirectory()
            = Paths.get(
                 settingsManager.getSettingValue(SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY)
                    ?: throw IllegalStateException("missing setting [${SystemSettings.BUILT_IN_BASIC_STEPS_DIRECTORY.key}]"
            )
    )

}
