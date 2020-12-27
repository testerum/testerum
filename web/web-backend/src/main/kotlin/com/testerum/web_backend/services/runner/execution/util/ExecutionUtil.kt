package com.testerum.web_backend.services.runner.execution.util

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.config.PathWithScenarioIndexes
import com.testerum.model.test.TestModel
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.tests_finder.TestPath

fun Map<TestPath, TestModel>.toPathWithScenarioIndexes(): List<PathWithScenarioIndexes> {
    val result = mutableListOf<PathWithScenarioIndexes>()
    for (key in this.keys) {
        result += when (key) {
            is ScenariosTestPath -> PathWithScenarioIndexes(Path.createInstance(key.javaPath.toString()), key.scenarioIndexes)
            else -> PathWithScenarioIndexes(Path.createInstance(key.javaPath.toString()), emptyList())
        }
    }

    return result
}
