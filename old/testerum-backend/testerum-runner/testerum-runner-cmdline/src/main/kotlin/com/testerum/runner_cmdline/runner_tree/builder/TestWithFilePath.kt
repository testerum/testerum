package com.testerum.runner_cmdline.runner_tree.builder

import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.model.tests_finder.TestPath

data class TestWithFilePath(val test: TestModel, val testPath: TestPath) : HasPath {

    override val path: Path
        get() = test.path


}
