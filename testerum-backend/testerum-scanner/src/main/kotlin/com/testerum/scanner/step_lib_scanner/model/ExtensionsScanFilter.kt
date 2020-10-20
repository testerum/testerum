package com.testerum.scanner.step_lib_scanner.model

data class ExtensionsScanFilter(
        /**
         * If non-empty, only these packages will be scanned.
         * This can speed-up the scanning very much.
         * If the list is empty, all packages will be scanned.
         */
        val onlyFromPackages: List<String> = emptyList()
)
