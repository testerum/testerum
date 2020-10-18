package com.testerum.file_service.caches.resolved

import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.list
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.scanner.step_lib_scanner.ExtensionsScanner
import com.testerum.scanner.step_lib_scanner.model.ExtensionsScanConfig
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.settings.SettingsManager
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class BasicStepsCache(private val persistentCacheManager: ExtensionsScanner,
                      private val settingsManager: SettingsManager) {

    companion object {
        private val LOG = LoggerFactory.getLogger(BasicStepsCache::class.java)
    }

    private val lock = ReentrantReadWriteLock()

    private var hooks: MutableList<HookDef> = mutableListOf()
    private var basicSteps: MutableList<BasicStepDef> = mutableListOf()

    private var stepsByPath: MutableMap<Path, BasicStepDef> = hashMapOf()

    fun getBasicSteps(): Collection<BasicStepDef> = lock.read { basicSteps }

    fun getHooks(): Collection<HookDef> = lock.read { hooks }

    fun getStepAtPath(basicStepPath: Path): BasicStepDef? = lock.read { stepsByPath[basicStepPath] }

    fun initialize(additionalBasicStepsDir: JavaPath) {
        lock.write {
            // load step libs
            val (steps, hooks) = loadStepLibs(additionalBasicStepsDir)
            this.basicSteps = ArrayList(steps)
            this.hooks = ArrayList(hooks)

            this.stepsByPath = run {
                val result = HashMap<Path, BasicStepDef>()

                for (basicStep in basicSteps) {
                    result[basicStep.path] = basicStep
                }

                result
            }
        }
    }

    private fun loadStepLibs(additionalBasicStepsDir: JavaPath): Pair<List<BasicStepDef>, List<HookDef>> {
        val startTimeMillis = System.currentTimeMillis()

        val scanResult = persistentCacheManager.scan(
            ExtensionsScanConfig(
                extraJars = additionalBasicStepsDir.list { it.hasExtension(".jar") }
            )
        )

        // register setting definitions
        settingsManager.modify {
            registerDefinitions(scanResult.settingDefinitions)
        }

        val steps: List<BasicStepDef> = scanResult.steps
        val hooks: List<HookDef> = scanResult.hooks

        val endTimeInitMillis = System.currentTimeMillis()
        LOG.info("loading step libraries (${steps.size} steps & ${hooks.size} hooks) took ${endTimeInitMillis - startTimeMillis} ms")

        return Pair(steps, hooks)
    }

}
