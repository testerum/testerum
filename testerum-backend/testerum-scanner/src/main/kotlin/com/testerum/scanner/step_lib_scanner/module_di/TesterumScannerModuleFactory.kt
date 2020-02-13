package com.testerum.scanner.step_lib_scanner.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.scanner.step_lib_scanner.ExtensionsScanner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.max

class TesterumScannerModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    private val stepLibraryCacheMangerThreadPool: ExecutorService = run {
        val executor = Executors.newFixedThreadPool(
                max(
                        Runtime.getRuntime().availableProcessors() - 1,
                        2
                )
        )

        context.registerShutdownHook {
            executor.shutdownNow()
        }

        executor
    }

    val extensionsScanner = ExtensionsScanner(stepLibraryCacheMangerThreadPool)

}
