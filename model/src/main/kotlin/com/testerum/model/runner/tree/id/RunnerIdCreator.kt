package com.testerum.model.runner.tree.id

import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.StepCall
import com.testerum.model.test.TestModel

object RunnerIdCreator {

    fun createRootId(): String {
        return "root"
    }

    fun createFeatureId(
        parentNodeId: String,
        nodePath: Path
    ): String {
        return "$parentNodeId/${nodePath.directories.last()}"
    }

    fun createTestId(
        parentNodeId: String,
        test: TestModel
    ): String {
        return "$parentNodeId/${test.id}"
    }

    fun createParametrizedTestId(
        parentNodeId: String,
        test: TestModel
    ): String {
        return "$parentNodeId/${test.id}"
    }

    fun createScenarioId(
        parentNodeId: String,
        nodeIndexInParent: Int,
        test: TestModel
    ): String {
        return "$parentNodeId/${test.id}-$nodeIndexInParent"
    }

    fun createHookContainerId(
        parentNodeId: String,
        hookPhase: HookPhase,
    ): String {
        return "$parentNodeId/$hookPhase"
    }

    fun createAfterTestHookContainerId(
        parentNodeId: String,
    ): String {
        return "$parentNodeId/AFTER_TEST"
    }

    fun createStepId(
        parentNodeId: String,
        nodeIndexInParent: Int,
        stepCall: StepCall
    ): String {
        return "$parentNodeId/$stepCall-$nodeIndexInParent"
    }
}
