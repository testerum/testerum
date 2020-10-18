package com.testerum.scanner.step_lib_scanner

import com.testerum.model.step.BasicStepDef
import com.testerum.scanner.step_lib_scanner.impl.getHookDefinitions
import com.testerum.scanner.step_lib_scanner.impl.getSettingDefinitions
import com.testerum.scanner.step_lib_scanner.impl.getStepDefinitions
import com.testerum.scanner.step_lib_scanner.model.ExtensionsScanConfig
import com.testerum.scanner.step_lib_scanner.model.ExtensionsScanResult
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingDefinition
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import org.slf4j.LoggerFactory
import java.net.URLClassLoader
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class ExtensionsScanner(private val threadPool: ExecutorService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ExtensionsScanner::class.java)

        private val availableProcessors = Runtime.getRuntime().availableProcessors()

        // copied from the classgraph library
        private val DEFAULT_NUM_WORKER_THREADS = max(
            // Always scan with at least 2 threads
            2, //
            ceil(
                // Num IO threads (top out at 4, since most I/O devices won't scale better than this)
                min(4.0, availableProcessors * 0.75) +
                    // Num scanning threads (higher than available processors, because some threads can be blocked)
                    availableProcessors * 1.25
            ).toInt() //
        )
    }

    fun scan(config: ExtensionsScanConfig): ExtensionsScanResult {
        LOG.info("starting to scan for extensions...")

        val steps = mutableListOf<BasicStepDef>()
        val hooks = mutableListOf<HookDef>()
        val settingDefinitions = mutableListOf<SettingDefinition>()

        val classGraph: ClassGraph = ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .enableMethodInfo()
            .overrideClassLoaders(
                stepsClassLoader(config)
            )
            .acceptPackages(*config.onlyFromPackages.toTypedArray())

        val startTime = System.nanoTime()
        classGraph.scan(threadPool, DEFAULT_NUM_WORKER_THREADS).use { scanResult: ScanResult ->
            val endTime = System.nanoTime()
            LOG.info("...done scanning for extensions; took ${TimeUnit.NANOSECONDS.toMillis(endTime - startTime)} ms")

            for (classInfo in scanResult.allClasses) {
                steps += classInfo.getStepDefinitions()
                hooks += classInfo.getHookDefinitions()
                settingDefinitions += classInfo.getSettingDefinitions()
            }
        }

        return ExtensionsScanResult(
            steps = steps,
            hooks = hooks,
            settingDefinitions = settingDefinitions
        )
    }

    private fun stepsClassLoader(config: ExtensionsScanConfig): ClassLoader {
        return URLClassLoader(
            Array(config.extraJars.size) { i ->
                config.extraJars[i].toUri().toURL()
            },
            Thread.currentThread().contextClassLoader
        )
    }

}
