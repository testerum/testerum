package com.testerum.scanner.step_lib_scanner.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.scanner.step_lib_scanner.StepLibraryCacheManger
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TesterumScannerModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    private val stepLibraryCacheMangerThreadPool: ExecutorService = run {
        val executor = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors() - 1
        )

        context.registerShutdownHook {
            executor.shutdownNow()
        }

        executor
    }

    val stepLibraryCacheManger = StepLibraryCacheManger(stepLibraryCacheMangerThreadPool)

}
