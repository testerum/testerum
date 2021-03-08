package com.testerum.model.runner.old_tree.builder

import com.testerum.model.test.TestModel
import com.testerum.model.tests_finder.TestPath

data class TestPathAndModel(
    val path: TestPath,
    val model: TestModel
)
