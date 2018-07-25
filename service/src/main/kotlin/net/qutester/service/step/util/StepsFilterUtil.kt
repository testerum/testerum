package net.qutester.service.step.util

import com.testerum.common.string.containsSearchStringParts
import net.qutester.model.step.BasicStepDef
import net.qutester.model.step.ComposedStepDef
import net.qutester.model.step.StepDef
import net.qutester.model.step.filter.StepsTreeFilter

object StepsFilterUtil {

    fun isStepMatchingFilter(step: StepDef, stepsTreeFilter: StepsTreeFilter): Boolean {
        val testMatchesTypeFilter = testMatchesTypeFilter(step, stepsTreeFilter)
        val testMatchesTestFilter = testMatchesSearchFilter(step, stepsTreeFilter)
        val testIsMatchTagsFilterCriteria = tagListMatchesTagsFilterCriteria(step.tags, stepsTreeFilter)

        if (testMatchesTypeFilter && testMatchesTestFilter && testIsMatchTagsFilterCriteria) {
            return true;
        }

        return false;
    }

    private fun testMatchesTypeFilter(step: StepDef, stepsTreeFilter: StepsTreeFilter): Boolean {
        return step is ComposedStepDef == stepsTreeFilter.showComposedTests ||
                step is BasicStepDef == stepsTreeFilter.showBasicTest
    }

    private fun tagListMatchesTagsFilterCriteria(tags: List<String>, stepsTreeFilter: StepsTreeFilter): Boolean {
        val upperCasedTags = tags.map(String::toUpperCase)

        return upperCasedTags.containsAll(
                stepsTreeFilter.tags.map(String::toUpperCase)
        )
    }

    private fun testMatchesSearchFilter(step: StepDef, stepsTreeFilter: StepsTreeFilter): Boolean {
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