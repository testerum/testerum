package com.testerum.model.tests_finder

import java.nio.file.Path as JavaPath

sealed class TestPath {
    abstract val javaPath: JavaPath
}

data class FeatureTestPath(val featureDir: JavaPath) : TestPath() {
    override val javaPath: JavaPath = featureDir
}

data class TestTestPath(val testFile: JavaPath): TestPath() {
    override val javaPath: JavaPath = testFile
}

data class ScenariosTestPath(val testFile: JavaPath, val scenarioIndexes: List<Int>): TestPath() {
    override val javaPath: JavaPath = testFile
}
