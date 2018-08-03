package net.qutester.service.step.util

import com.testerum.common.string.containsSearchStringParts
import net.qutester.model.step.BasicStepDef
import net.qutester.model.step.ComposedStepDef
import net.qutester.model.step.StepDef
import net.qutester.model.step.filter.StepsTreeFilter

object StepsFilterUtil {

    fun isStepMatchingFilter(step: StepDef, stepsTreeFilter: StepsTreeFilter): Boolean {
        val stepMatchesTypeFilter = stepMatchesTypeFilter(step, stepsTreeFilter)
        val stepMatchesTestFilter = stepMatchesSearchFilter(step, stepsTreeFilter)
        val stepIsMatchTagsFilterCriteria = tagListMatchesTagsFilterCriteria(step.tags, stepsTreeFilter)

        if (stepMatchesTypeFilter && stepMatchesTestFilter && stepIsMatchTagsFilterCriteria) {
            return true
        }

        return false
    }

    private fun stepMatchesTypeFilter(step: StepDef, stepsTreeFilter: StepsTreeFilter): Boolean {
        return step is ComposedStepDef == stepsTreeFilter.showComposedTests ||
                step is BasicStepDef == stepsTreeFilter.showBasicTest
    }

    private fun tagListMatchesTagsFilterCriteria(tags: List<String>, stepsTreeFilter: StepsTreeFilter): Boolean {
        val upperCasedTags = tags.map(String::toUpperCase)

        return upperCasedTags.containsAll(
                stepsTreeFilter.tags.map(String::toUpperCase)
        )
    }

    private fun stepMatchesSearchFilter(step: StepDef, stepsTreeFilter: StepsTreeFilter): Boolean {
        var stepMatchesTestFilter = false

        if (step.path.toString().containsSearchStringParts(stepsTreeFilter.search)) {
            stepMatchesTestFilter = true
        }

        if (step.getText().containsSearchStringParts(stepsTreeFilter.search)) {
            stepMatchesTestFilter = true
        }
        return stepMatchesTestFilter
    }

}