package com.testerum.report_generators.reports.report_model.base.mapper

import com.testerum.model.arg.Arg
import com.testerum.model.step.*
import com.testerum.runner.report_model.*
import kotlin.reflect.jvm.jvmName

object ReportStepDefAndCallMapper {

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
                content = arg.contentForLogging,
                type = arg.typeMeta.javaType,
                path = arg.path?.toString()
        )
    }

}
