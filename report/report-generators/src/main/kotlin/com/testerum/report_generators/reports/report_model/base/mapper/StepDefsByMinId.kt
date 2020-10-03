package com.testerum.report_generators.reports.report_model.base.mapper

import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepDef
import com.testerum.runner.report_model.ReportStepDef
import com.testerum.report_generators.reports.report_model.base.mapper.utils.SpaceMinimizingIdGenerator

class StepDefsByMinId {
    private val minIdGenerator = SpaceMinimizingIdGenerator()
    private val minIdByOriginalId = HashMap</*originalId: */String, /*minId: */String>()
    private val stepDefsByMinId = HashMap</*minId: */String, ReportStepDef>()

    fun getMap() = stepDefsByMinId

    /**
     * @return the id of the added step def
     */
    fun addStepDefsRecursively(stepDef: StepDef): String {
        val existingMinId = minIdByOriginalId[stepDef.id]
        if (existingMinId != null) {
            return existingMinId
        }

        val newMinId = minIdGenerator.nextId()
        minIdByOriginalId[stepDef.id] = newMinId
        stepDefsByMinId[newMinId] = ReportStepDefAndCallMapper.mapStepDef(stepDef, this)

        if (stepDef is ComposedStepDef) {
            for (stepCall in stepDef.stepCalls) {
                addStepDefsRecursively(stepCall.stepDef)
            }
        }

        return newMinId
    }
}
