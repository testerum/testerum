package com.testerum.web_backend.services.manual.filterer

import com.testerum.common_jdk.containsSearchStringParts
import com.testerum.model.manual.ManualTest
import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.model.manual.status_tree.filter.ManualTreeStatusFilter

object ManualTestsTreeFilterer {

    fun filterTests(tests: List<ManualTest>,
                    filter: ManualTreeStatusFilter): List<ManualTest> {
        val result = mutableListOf<ManualTest>()

        for (test in tests) {
            if (!matchesStatus(test.status, filter)) {
                continue
            }
            if (!matchesTextSearch(test, filter.search)) {
                continue
            }
            if (!matchesTags(test.tags, filter.tags)) {
                continue
            }

            result += test
        }

        return result
    }

    private fun matchesTags(tags: List<String>, filterTags: List<String>): Boolean {
        val upperCasedTags = tags.map(String::toUpperCase)

        return upperCasedTags.containsAll(
                filterTags.map(String::toUpperCase)
        )
    }

    private fun matchesStatus(status: ManualTestStatus, filter: ManualTreeStatusFilter): Boolean {
        return when (status) {
            ManualTestStatus.NOT_EXECUTED   -> filter.showNotExecuted
            ManualTestStatus.IN_PROGRESS    -> true
            ManualTestStatus.BLOCKED        -> filter.showBlocked
            ManualTestStatus.FAILED         -> filter.showFailed
            ManualTestStatus.PASSED         -> filter.showPassed
            ManualTestStatus.NOT_APPLICABLE -> filter.showNotApplicable
        }
    }

    private fun matchesTextSearch(test: ManualTest, filterSearch: String?): Boolean {
        if (filterSearch == null) {
            return true // not filtering on text
        }

        if (test.path.toString().containsSearchStringParts(filterSearch)) {
            return true
        }
        if (test.name.containsSearchStringParts(filterSearch)) {
            return true
        }
        if (test.description.containsSearchStringParts(filterSearch)) {
            return true
        }
        if (test.comments.containsSearchStringParts(filterSearch)) {
            return true
        }

        return false
    }

}