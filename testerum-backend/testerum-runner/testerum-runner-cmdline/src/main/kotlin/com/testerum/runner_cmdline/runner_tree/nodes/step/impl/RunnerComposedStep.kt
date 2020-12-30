package com.testerum.runner_cmdline.runner_tree.nodes.step.impl

import com.testerum.common_kotlin.indent
import com.testerum.model.arg.Arg
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.StepDef
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.step.RunnerStep
import com.testerum.runner_cmdline.runner_tree.runner_context.RunnerContext
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.PASSED
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus.UNDEFINED

class RunnerComposedStep(
    parent: TreeNode,
    stepCall: StepCall,
    indexInParent: Int,
    logEvents: Boolean,
) : RunnerStep(parent, stepCall, indexInParent, logEvents) {

    private val children = mutableListOf<RunnerStep>()

    fun addChild(child: RunnerStep) {
        this.children += child
    }

    override fun doRun(context: RunnerContext): ExecutionStatus {
        if (children.isEmpty()) {
            val executionStatus = UNDEFINED
            context.logMessage("marking composed step [${(stepCall.stepDef as ComposedStepDef).path}] as $executionStatus because it doesn't have any child steps")

            return executionStatus
        }

        var status: ExecutionStatus = PASSED

        val resolvedArgs = resolveArgs(stepCall, context.variablesContext)
        context.variablesContext.startComposedStep()
        setArgs(context.variablesContext, resolvedArgs)

        status = runChildren(context, status)

        context.variablesContext.endComposedStep()

        return status
    }

    private fun resolveArgs(
        stepCall: StepCall,
        variablesContext: VariablesContext
    ): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        val stepDef: StepDef = stepCall.stepDef

        val params: List<ParamStepPatternPart> = stepDef.stepPattern.getParamStepPattern()
        val args: List<Arg> = stepCall.args

        for (i in params.indices) {
            val param: ParamStepPatternPart = params[i]
            val arg: Arg = args[i]

            val paramName: String = param.name

            result[paramName] = variablesContext.resolveIn(arg)
        }

        return result
    }

    private fun setArgs(
        variablesContext: VariablesContext,
        args: Map<String, Any?>,
    ) {
        for ((name, value) in args) {
            variablesContext.setArg(name, value)
        }
    }

    private fun runChildren(
        context: RunnerContext,
        overallStatus: ExecutionStatus,
    ): ExecutionStatus {
        var status = overallStatus

        for (child in children) {
            if (status <= PASSED) {
                val childStatus: ExecutionStatus = child.execute(context)

                if (childStatus > status) {
                    status = childStatus
                }
            } else {
                child.skip(context)
            }
        }

        return status
    }

    override fun doSkip(context: RunnerContext) {
        for (step in children) {
            step.skip(context)
        }
    }

    override fun doDisable(context: RunnerContext) {
        for (step in children) {
            step.disable(context)
        }
    }

    override fun toString(): String = buildString { addToString(this, 0) }

    override fun addToString(destination: StringBuilder, indentLevel: Int) {
        destination.indent(indentLevel).append("step ")
        stepCall.toString(destination, 0)
        destination.append("\n")

        for (step in children) {
            step.addToString(destination, indentLevel + 1)
        }
    }

}
