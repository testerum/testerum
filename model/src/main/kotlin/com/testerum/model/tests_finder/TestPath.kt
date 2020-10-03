package com.testerum.model.tests_finder

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.nio.file.Path as JavaPath


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = FeatureTestPath::class    , name = "FEATURE_TEST_PATH"),
    JsonSubTypes.Type(value = TestTestPath::class       , name = "TEST_TEST_PATH"),
    JsonSubTypes.Type(value = ScenariosTestPath::class  , name = "SCENARIOS_TEST_PATH")
])
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
