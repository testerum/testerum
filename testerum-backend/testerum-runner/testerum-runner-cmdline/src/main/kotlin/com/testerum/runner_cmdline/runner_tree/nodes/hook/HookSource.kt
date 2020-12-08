package com.testerum.runner_cmdline.runner_tree.nodes.hook

import com.testerum.model.infrastructure.path.Path

sealed class HookSource

data class FeatureHookSource(
    val featurePath: Path,
) : HookSource() {
    override fun toString(): String = "Feature($featurePath)"
}

data class TestHookSource(
    val testPath: Path,
) : HookSource() {
    override fun toString(): String = "Test($testPath)"
}

data class BasicHookSource(
    val className: String,
    val methodName: String,
) : HookSource() {
    override fun toString(): String = "Basic($className.$methodName())"
}
