package com.testerum.project_manager.dirs

import java.nio.file.Path as JavaPath

class ProjectDirs(val projectRootDir: JavaPath) {

    fun getFeaturesDir(): JavaPath = projectRootDir.resolve("features")

    fun getTestsDir(): JavaPath = getFeaturesDir()

    fun getManualTestsDir(): JavaPath = projectRootDir.resolve("manual_tests")

    fun getComposedStepsDir(): JavaPath = projectRootDir.resolve("composed_steps")

    fun getResourcesDir(): JavaPath = projectRootDir.resolve("resources")

    fun getVariablesDir(): JavaPath = projectRootDir.resolve("variables")

}
