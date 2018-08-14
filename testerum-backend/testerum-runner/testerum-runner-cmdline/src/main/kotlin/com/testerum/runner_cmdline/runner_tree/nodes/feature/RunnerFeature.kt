package com.testerum.runner_cmdline.runner_tree.nodes.feature

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_kotlin.indent
import com.testerum.model.feature.Feature
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.error.ExceptionDetail
import com.testerum.runner.events.model.position.PositionInParent
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.GlobalVariablesContext

class RunnerFeature(featurePathFromRoot: List<String>,
                    private val featureName: String,
                    private val featuresOrTests: List<RunnerFeatureOrTest>,
                    indexInParent: Int): RunnerFeatureOrTest() {

    override lateinit var parent: RunnerTreeNode
    override val positionInParent = PositionInParent(
            id= featurePathFromRoot.joinToString(separator = "/") + "/${Feature.FILE_NAME_WITH_EXTENSION}",
            indexInParent = indexInParent
    )

    init {
        for (featureOrTest in featuresOrTests) {
            featureOrTest.parent = this
        }
    }

    override fun getGlueClasses(context: RunnerContext): List<Class<*>> {
        val result = ArrayList<Class<*>>()

        for (featuresOrTest in featuresOrTests) {
            result += featuresOrTest.getGlueClasses(context)
        }

        return result
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

        var executionStatus: ExecutionStatus = ExecutionStatus.PASSED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (featuresOrTest in featuresOrTests) {
                val stepExecutionStatus: ExecutionStatus = featuresOrTest.run(context, globalVars)

                executionStatus = stepExecutionStatus
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logFeatureEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
        }

        return executionStatus
    }

    override fun skip(context: RunnerContext): ExecutionStatus {
        logFeatureStart(context)

        var executionStatus = ExecutionStatus.SKIPPED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (featureOrTest in featuresOrTests) {
                featureOrTest.skip(context)
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logFeatureEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
            return executionStatus
        }
    }

    override fun disable(context: RunnerContext): ExecutionStatus {
        logFeatureStart(context)

        var executionStatus = ExecutionStatus.DISABLED
        var exception: Throwable? = null

        val startTime = System.currentTimeMillis()
        try {
            for (featureOrTest in featuresOrTests) {
                featureOrTest.disable(context)
            }
        } catch (e: Exception) {
            executionStatus = ExecutionStatus.FAILED
            exception = e
        } finally {
            logFeatureEnd(context, executionStatus, exception, durationMillis = System.currentTimeMillis() - startTime)
            return executionStatus
        }
    }

    private fun logFeatureStart(context: RunnerContext) {
        context.eventsService.logEvent(
                FeatureStartEvent(
                        eventKey = eventKey,
                        featureName = featureName
                )
        )
    }

    private fun logFeatureEnd(context: RunnerContext,
                             executionStatus: ExecutionStatus,
                             exception: Throwable?,
                             durationMillis: Long) {
        context.eventsService.logEvent(
                FeatureEndEvent(
                        eventKey = eventKey,
                        featureName = featureName,
                        status = executionStatus,
                        exceptionDetail = exception?.let { ExceptionDetail.fromThrowable(it) },
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
