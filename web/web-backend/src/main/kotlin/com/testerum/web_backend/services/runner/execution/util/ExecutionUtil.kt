package com.testerum.web_backend.services.runner.execution.util

import com.testerum.common_kotlin.canonicalize
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.config.PathWithScenarioIndexes
import com.testerum.model.test.TestModel
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.tests_finder.TestPath
import java.nio.file.Path as JavaPath

fun Map<TestPath, TestModel>.toPathWithScenarioIndexes(featuresDir: JavaPath): List<PathWithScenarioIndexes> {
    val result = mutableListOf<PathWithScenarioIndexes>()

    for (key in this.keys) {
        val path = Path.createInstance(
            key.javaPath.canonicalize()
                .relativize(featuresDir)
                .toString()
        )
        result += when (key) {
            is ScenariosTestPath -> PathWithScenarioIndexes(path, key.scenarioIndexes)
            else -> PathWithScenarioIndexes(path, emptyList())
        }
    }

    return result
}
