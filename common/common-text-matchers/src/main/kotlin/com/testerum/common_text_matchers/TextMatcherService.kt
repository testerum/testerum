package com.testerum.common_text_matchers

import com.testerum.common_text_matchers.matchers.CaseInsensitiveTextMatcher
import com.testerum.common_text_matchers.matchers.ContainsCaseInsensitiveTextMatcher
import com.testerum.common_text_matchers.matchers.ContainsCaseSensitiveTextMatcher
import com.testerum.common_text_matchers.matchers.ExactTextMatcher
import com.testerum.common_text_matchers.matchers.RegexTextMatcher
import com.testerum.common_text_matchers.matchers.TextMatcher

object TextMatcherService {

    // todo: make these strategies pluggable
    private val prefixToTextMatcherMap: Map<String, TextMatcher> = mapOf(
            "exact"                   to ExactTextMatcher,
            "caseInsensitive"         to CaseInsensitiveTextMatcher,
            "contains"                to ContainsCaseSensitiveTextMatcher,
            "containsCaseInsensitive" to ContainsCaseInsensitiveTextMatcher,
            "regex"                   to RegexTextMatcher
    )

    private val validPrefixes: List<String> = prefixToTextMatcherMap.keys.map { it + "=" }

    fun doesNotMatch(expectedTextMatchExpression: String, actualText: String): Boolean = !matches(expectedTextMatchExpression, actualText)

    fun matches(expectedTextMatchExpression: String, actualText: String): Boolean {
        val indexOfEquals: Int = expectedTextMatchExpression.indexOf("=")

        if (indexOfEquals == -1) {
            return ContainsCaseSensitiveTextMatcher.matches(expectedTextMatchExpression, actualText)
        }

        val textMatcherType: String = expectedTextMatchExpression.substring(0, indexOfEquals)

        val textMatcher = prefixToTextMatcherMap[textMatcherType]

        if(textMatcher == null) {
            return ContainsCaseSensitiveTextMatcher.matches(expectedTextMatchExpression, actualText)
        }

        val expressionWithoutPrefix = if (indexOfEquals == expectedTextMatchExpression.length - 1) {
            ""
        } else {
            expectedTextMatchExpression.substring(indexOfEquals + 1)
        }

        return textMatcher.matches(expressionWithoutPrefix, actualText)
    }

}
