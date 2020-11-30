package com.testerum.runner_cmdline.runner_tree.nodes.feature

import com.testerum.common_kotlin.indent
import com.testerum.model.feature.Feature
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.PASSED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.SKIPPED

class RunnerFeature(
    featurePathFromRoot: List<String>,
    val featureName: String,
    val tags: List<String>,
    val featuresOrTests: List<RunnerFeatureOrTest>,
    indexInParent: Int
) : RunnerFeatureOrTest() {

    override lateinit var parent: RunnerTreeNode

    override val positionInParent = PositionInParent(
        id = featurePathFromRoot.joinToString(separator = "/") + "/${Feature.FILE_NAME_WITH_EXTENSION}",
        indexInParent = indexInParent
    )

    init {
        for (featureOrTest in featuresOrTests) {
            featureOrTest.parent = this
        }
    }

    override fun run(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        try {
            return tryToRun(context, globalVars)
        } catch (e: Exception) {
            throw RuntimeException("failed to execute feature [$featureName]", e)
        }
    }

    private fun tryToRun(context: RunnerContext, globalVars: GlobalVariablesContext): ExecutionStatus {
        logFeatureStart(context)

        var status: ExecutionStatus = PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (featureOrTest in featuresOrTests) {
                val featureOrTestStatus: ExecutionStatus = featureOrTest.run(context, globalVars)

                if (featureOrTestStatus > status) {
                    status = featureOrTestStatus
                }
            }
        } catch (e: Exception) {
            status = ExecutionStatus.FAILED
            exception = e
        } finally {
            logFeatureEnd(context, status, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return status
    }

    override fun skip(context: RunnerContext): ExecutionStatus {
        logFeatureStart(context)

        var status = SKIPPED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (featureOrTest in featuresOrTests) {
                val featureOrTestStatus: ExecutionStatus = featureOrTest.skip(context)

                if (featureOrTestStatus > status) {
                    status = featureOrTestStatus
                }
            }
        } catch (e: Exception) {
            status = ExecutionStatus.FAILED
            exception = e
        } finally {
            logFeatureEnd(context, status, exception, durationMillis = System.currentTimeMillis() - startTime)
            return status
        }
    }

    private fun logFeatureStart(context: RunnerContext) {
        context.logEvent(
            FeatureStartEvent(
                eventKey = eventKey,
                featureName = featureName,
                tags = tags
            )
        )
        context.logMessage("Started executing feature [$featureName]")
    }

    private fun logFeatureEnd(
        context: RunnerContext,
        executionStatus: ExecutionStatus,
        exception: Throwable?,
        durationMillis: Long
    ) {
        context.logMessage("Finished executing feature [$featureName]; status: [$executionStatus]", exception)
        context.logEvent(
            FeatureEndEvent(
                eventKey = eventKey,
                featureName = featureName,
                status = executionStatus,
                durationMillis = durationMillis
            )
        )
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("feature '").append(featureName).append("'\n")

        for (featureOrTest in featuresOrTests) {
            featureOrTest.addToString(destination, indentLevel + 1)
        }
    }

}
