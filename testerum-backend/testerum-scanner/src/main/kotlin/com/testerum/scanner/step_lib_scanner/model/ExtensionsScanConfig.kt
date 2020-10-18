package com.testerum.scanner.step_lib_scanner.model

import java.nio.file.Path as JavaPath

data class ExtensionsScanConfig(
    /**
     * Extra step library jars to load, in addition to
     * scanning the classpath.
     */
    val extraJars: List<JavaPath> = emptyList(),

    /**
     * If non-empty, only these packages will be scanned.
     * This can speed-up the scanning very much.
     * If the list is empty, all packages will be scanned.
     */
    val onlyFromPackages: List<String> = emptyList(),
)
