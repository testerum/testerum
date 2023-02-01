package com.testerum.runner_cmdline.cmdline.params

import com.testerum.model.tests_finder.TestPath
import com.testerum.runner_cmdline.cmdline.params.model.RunCmdlineParams
import java.nio.file.Path as JavaPath

class CmdlineParamsBuilder {

    private var verbose: Boolean = false
    private var repositoryDirectory: JavaPath? = null
    private val packagesWithAnnotations = mutableListOf<String>()
    private val testPaths = mutableListOf<TestPath>()
    private val includeTags = mutableListOf<String>()
    private val excludeTags = mutableListOf<String>()
    private var managedReportsDir: JavaPath? = null
    private val reportsWithProperties = mutableListOf<String>()
    private val settingOverrides = mutableMapOf<String, String>()
    private var settingsFile: JavaPath? = null
    private val variableOverrides = mutableMapOf<String, String>()
    private var variablesEnvironment: String? = null
    private var executionName: String? = null

    fun verbose() {
        verbose = true
    }

    fun repositoryDirectory(): JavaPath? = repositoryDirectory

    fun repositoryDirectory(dir: JavaPath) {
        repositoryDirectory = dir
    }

    fun addPackageWithAnnotations(packageWithAnnotations: String) {
        packagesWithAnnotations += packageWithAnnotations
    }

    fun addTestPath(testPath: TestPath) {
        testPaths += testPath
    }

    fun addIncludeTag(includeTag: String) {
        includeTags += includeTag
    }

    fun addExcludeTag(excludeTag: String) {
        excludeTags += excludeTag
    }

    fun managedReportsDir(managedReportsDir: JavaPath) {
        this.managedReportsDir = managedReportsDir
    }

    fun addReportWithProperties(reportWithProperties: String) {
        reportsWithProperties += reportWithProperties
    }

    fun overrideSetting(name: String, value: String) {
        settingOverrides[name] = value
    }

    fun settingsFile(settingsFile: JavaPath) {
        this.settingsFile = settingsFile
    }

    fun overrideVariable(name: String, value: String) {
        variableOverrides[name] = value
    }

    fun variablesEnvironment(variablesEnvironment: String) {
        this.variablesEnvironment = variablesEnvironment
    }

    fun executionName(executionName: String) {
        this.executionName = executionName
    }

    fun build(): RunCmdlineParams {
        val repositoryDirectory = this.repositoryDirectory
            ?: throw IllegalStateException("missing required argument repositoryDirectory")

        return RunCmdlineParams(
            verbose,
            repositoryDirectory,
            testPaths,
            packagesWithAnnotations,
            includeTags,
            excludeTags,
            managedReportsDir,
            reportsWithProperties,
            settingOverrides,
            settingsFile,
            variableOverrides,
            variablesEnvironment,
            executionName
        )
    }
}
