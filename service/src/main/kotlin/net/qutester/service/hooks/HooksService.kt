package net.qutester.service.hooks

import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import net.qutester.service.scanner.ScannerService

class HooksService(private val scannerService: ScannerService) {

    private val _hooks: List<HookDef> by lazy {
        scannerService.getHooks()
    }

    fun getHooks(): List<HookDef> = _hooks

}