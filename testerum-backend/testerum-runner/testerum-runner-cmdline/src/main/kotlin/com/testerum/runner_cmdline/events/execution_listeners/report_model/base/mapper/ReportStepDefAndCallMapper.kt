package com.testerum.runner_cmdline.events.execution_listeners.report_model.base.mapper

import com.testerum.model.arg.Arg
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.StepDef
import com.testerum.model.step.UndefinedStepDef
import com.testerum.runner.report_model.ReportBasicStepDef
import com.testerum.runner.report_model.ReportComposedStepDef
import com.testerum.runner.report_model.ReportStepCall
import com.testerum.runner.report_model.ReportStepCallArg
import com.testerum.runner.report_model.ReportStepDef
import com.testerum.runner.report_model.ReportUndefinedStepDef
import kotlin.reflect.jvm.jvmName

object ReportStepDefAndCallMapper {

    private val MAX_ARG_CONTENT_LENGTH = 80

    fun mapStepDef(stepDef: StepDef,
                           stepDefsByMinId: StepDefsByMinId) : ReportStepDef {
        val minStepDefId = stepDefsByMinId.addStepDefsRecursively(stepDef)

        return when (stepDef) {
            is UndefinedStepDef -> ReportUndefinedStepDef(
                    id = minStepDefId,
                    phase = stepDef.phase,
                    stepPattern = stepDef.stepPattern
            )
            is BasicStepDef -> ReportBasicStepDef(
                    id = minStepDefId,
                    path = stepDef.path.toString(),
                    phase = stepDef.phase,
                    stepPattern = stepDef.stepPattern,
                    description = stepDef.description,
                    tags = stepDef.tags,
                    className = stepDef.className,
                    methodName = stepDef.methodName
            )
            is ComposedStepDef -> ReportComposedStepDef(
                    id = minStepDefId,
                    path = stepDef.path.toString(),
                    phase = stepDef.phase,
                    stepPattern = stepDef.stepPattern,
                    description = stepDef.description,
                    tags = stepDef.tags,
                    stepCalls = stepDef.stepCalls.map {
                        mapStepCall(it, stepDefsByMinId)
                    }
            )
            else -> throw IllegalArgumentException("unknown step def type [${stepDef::class.jvmName}]")
        }
    }

    fun mapStepCall(stepCall: StepCall,
                    stepDefsByMinId: StepDefsByMinId): ReportStepCall {
        val minStepDefId = stepDefsByMinId.addStepDefsRecursively(stepCall.stepDef)

        return ReportStepCall(
                id = stepCall.id,
                stepDefId = minStepDefId,
                args = mapStepCallArgs(stepCall.args)
        )
    }

    private fun mapStepCallArgs(args: List<Arg>): List<ReportStepCallArg> {
        return args.map {
            mapStepCallArg(it)
        }
    }

    private fun mapStepCallArg(arg: Arg): ReportStepCallArg {

        return ReportStepCallArg(
                name = arg.name,
                content = cleanupArgContent(arg),
                type = arg.type,
                path = arg.path?.toString()
        )
    }

    private fun cleanupArgContent(arg: Arg): String? {
        var content = arg.content

        // remove content of external arg
        if (arg.path != null) {
            content = null
        }

        // remove content containing newlines
        if (content != null && content.contains('\n')) {
            content = null
        }

        // remove long content
        if (content != null && content.length > MAX_ARG_CONTENT_LENGTH) {
            content = content.substring(0, MAX_ARG_CONTENT_LENGTH) + "..."
        }

        return content
    }

}