package com.testerum.service.scanner

import com.testerum.api.test_context.settings.model.resolvedValueAsPath
import com.testerum.model.step.BasicStepDef
import com.testerum.scanner.step_lib_scanner.StepLibraryCacheManger
import com.testerum.scanner.step_lib_scanner.model.ScannerBasicStepScanResult
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.settings.SettingsManager
import com.testerum.settings.getRequiredSetting
import com.testerum.settings.keys.SystemSettingKeys
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate
import java.util.stream.Collectors

class ScannerService(private val settingsManager: SettingsManager,
                     private val stepLibraryCacheManger: StepLibraryCacheManger) {

    companion object {
        private val TESTERUM_DIRECTORY: Path = Paths.get(System.getProperty("user.home") + "/.testerum")
        private val cacheFile: Path = TESTERUM_DIRECTORY.resolve("cache/basic-steps-cache.json")
    }

    fun init() {
        settingsManager.modify {
            for (library in scanResult.libraries) {
                registerDefinitions(library.settingDefinitions)
            }
        }
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
        val basicStepsDirectories: Path = settingsManager.getRequiredSetting(SystemSettingKeys.BUILT_IN_BASIC_STEPS_DIR)
                                                         .resolvedValueAsPath

        val isJarFile = BiPredicate { file: Path, _: BasicFileAttributes ->
            Files.isRegularFile(file) && file.toString().endsWith(".jar")
        }

        Files.find(basicStepsDirectories, 1, isJarFile).use { stream ->
            return stream.collect(Collectors.toList())
        }
    }

}
