package com.testerum.scanner.step_lib_scanner

import com.testerum.model.step.BasicStepDef
import com.testerum.scanner.step_lib_scanner.model.ExtensionsScanConfig
import com.testerum.scanner.step_lib_scanner.model.ExtensionsScanResult
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingDefinition
import java.nio.file.Path
import java.util.concurrent.Executors

class ExtensionsLoaderService(private val extensionsCacheLoader: ExtensionsCacheLoader) {

    fun loadExtensions(
        packagesWithAnnotations: List<String>,
        additionalBasicStepsDirs: List<Path>
    ): ExtensionsScanResult {
        val extensionsFromCache = extensionsCacheLoader.loadCache(additionalBasicStepsDirs)
        if (packagesWithAnnotations.isEmpty()) {
            return extensionsFromCache
        }

        val extensionsFromAnnotationsScanning = scanPackages(packagesWithAnnotations)

        return aggregateResults(extensionsFromCache, extensionsFromAnnotationsScanning)
    }

    private fun scanPackages(packagesWithAnnotations: List<String>): ExtensionsScanResult {
        val threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        try {
            val extensionsScanner = ExtensionsScanner(threadPool)

            return extensionsScanner.scan(
                ExtensionsScanConfig(
                    onlyFromPackages = packagesWithAnnotations,
                    ignoreParentClassLoaders = true
                )
            )
        } finally {
            threadPool.shutdownNow()
            // no need for threadPool.awaitExecution() - assuming the scanner properly consumed all threads that it started
        }
    }

    private fun aggregateResults(
        extensionsFromCache: ExtensionsScanResult,
        extensionsFromAnnotationsScanning: ExtensionsScanResult
    ): ExtensionsScanResult {
        val steps = ArrayList<BasicStepDef>(
            extensionsFromCache.steps.size + extensionsFromAnnotationsScanning.steps.size
        )
        val hooks = ArrayList<HookDef>(
            extensionsFromCache.hooks.size + extensionsFromAnnotationsScanning.hooks.size
        )
        val settingDefinitions = ArrayList<SettingDefinition>(
            extensionsFromCache.settingDefinitions.size + extensionsFromAnnotationsScanning.settingDefinitions.size
        )

        steps += extensionsFromCache.steps
        steps += extensionsFromAnnotationsScanning.steps

        hooks += extensionsFromCache.hooks
        hooks += extensionsFromAnnotationsScanning.hooks

        settingDefinitions += extensionsFromCache.settingDefinitions
        settingDefinitions += extensionsFromAnnotationsScanning.settingDefinitions

        return ExtensionsScanResult(steps, hooks, settingDefinitions)
    }

}
