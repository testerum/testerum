package com.testerum.model.runner.tree

import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.model.tests_finder.TestPath

data class TestPathAndModel(
    val testPath: TestPath,
    val model: TestModel
): HasPath {
    override val path: Path
        get() = model.path
}
