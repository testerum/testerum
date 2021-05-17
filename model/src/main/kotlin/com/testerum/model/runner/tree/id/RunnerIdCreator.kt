package com.testerum.model.runner.tree.id

import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.StepCall
import com.testerum.model.test.TestModel

object RunnerIdCreator {

    fun getRootId(): String {
        return "root"
    }

    fun getFeatureId(
        parentNodeId: String,
        nodePath: Path
    ): String {
        return "$parentNodeId/${nodePath.directories.last()}"
    }

    fun getTestId(
        test: TestModel
    ): String {
        return "${getRootId()}/${test.id}"
    }

    fun getParametrizedTestId(
        test: TestModel
    ): String {
        return "${getRootId()}/${test.id}"
    }

    fun getScenarioId(
        nodeIndexInParent: Int,
        test: TestModel
    ): String {
        return "${getRootId()}/${test.id}-$nodeIndexInParent"
    }

    fun getHookContainerId(
        parentNodeId: String,
        hookPhase: HookPhase,
    ): String {
        return "$parentNodeId/$hookPhase"
    }

    fun getAfterTestHookContainerId(
        parentNodeId: String,
    ): String {
        return "$parentNodeId/AFTER_TEST"
    }

    fun getStepId(
        parentNodeId: String,
        nodeIndexInParent: Int,
        stepCall: StepCall
    ): String {
        return "$parentNodeId/$stepCall-$nodeIndexInParent"
    }
}
