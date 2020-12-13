package com.testerum.scanner.step_lib_scanner

import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.list
import com.testerum.common_kotlin.toUrlArray
import com.testerum.model.step.BasicStepDef
import com.testerum.scanner.step_lib_scanner.model.ExtensionsScanResult
import com.testerum.scanner.step_lib_scanner.model.cache_marshalling.ExtensionsScanResultMarshaller
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingDefinition
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path as JavaPath

class ExtensionsCacheLoader {

    companion object {
        const val CACHE_PATH_IN_JAR = "META-INF/testerum/extensions-info.tfsf"
    }

    fun loadCache(additionalBasicStepsDirs: List<JavaPath>): ExtensionsScanResult {
        val additionalJars = additionalBasicStepsDirs.flatMap { dir ->
            dir.list { it.hasExtension(".jar") }
        }.toUrlArray()

        val classLoader = URLClassLoader(
            additionalJars,
            Thread.currentThread().contextClassLoader
        )

        return classLoader.getResources(CACHE_PATH_IN_JAR)
            .toList()
            .map { loadCacheFromResource(it) }
            .aggregate()
    }

    private fun loadCacheFromResource(resourceUrl: URL): ExtensionsScanResult {
        return resourceUrl.openStream().use { inputStream ->
            val fastInput = FastInput.readFrom(inputStream)

            ExtensionsScanResultMarshaller.parse("", fastInput)
        }
    }

    private fun Iterable<ExtensionsScanResult>.aggregate(): ExtensionsScanResult {
        val steps = mutableListOf<BasicStepDef>()
        val hooks = mutableListOf<HookDef>()
        val settingDefinitions = mutableListOf<SettingDefinition>()

        for (scanResult in this) {
            steps += scanResult.steps
            hooks += scanResult.hooks
            settingDefinitions += scanResult.settingDefinitions
        }

        return ExtensionsScanResult(steps, hooks, settingDefinitions)
    }
}
