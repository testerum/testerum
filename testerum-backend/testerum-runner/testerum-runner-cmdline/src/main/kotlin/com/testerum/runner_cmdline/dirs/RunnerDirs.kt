package com.testerum.runner_cmdline.dirs

import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.isNotADirectory
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

object RunnerDirs {

    fun getPackageDir(): JavaPath = run {
        val packageDirectoryProperty = System.getProperty("testerum.packageDirectory")
                ?: throw IllegalArgumentException("missing required [testerum.packageDirectory] system property")

        val path: JavaPath = Paths.get(packageDirectoryProperty)
        val absolutePath: JavaPath = path.toAbsolutePath().normalize()

        if (absolutePath.doesNotExist) {
            throw IllegalArgumentException("package directory [$path] (resolved as absolutePath) does not exist")
        }
        if (absolutePath.isNotADirectory) {
            throw IllegalArgumentException("package directory [$path] (resolved as absolutePath) is not a directory")
        }

        return@run absolutePath
    }

    fun getRunnerDir(): JavaPath = getPackageDir().resolve("runner")

    fun getRunnerNodeDir(): JavaPath = getRunnerDir().resolve("node")

    fun getReportTemplatesDir(): JavaPath = getRunnerDir().resolve("report_templates")

}
