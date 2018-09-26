package selenium_steps_support.service.text_match

import selenium_steps_support.service.text_match.matchers.CaseInsensitiveTextMatcher
import selenium_steps_support.service.text_match.matchers.ContainsCaseInsensitiveTextMatcher
import selenium_steps_support.service.text_match.matchers.ContainsCaseSensitiveTextMatcher
import selenium_steps_support.service.text_match.matchers.ExactTextMatcher
import selenium_steps_support.service.text_match.matchers.RegexTextMatcher
import selenium_steps_support.service.text_match.matchers.TextMatcher

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

        val textMatcher = (prefixToTextMatcherMap[textMatcherType]
                ?: throw IllegalArgumentException("invalid text match expression; it must start with one of ${validPrefixes}, but got $textMatcherType"))

        val expressionWithoutPrefix = if (indexOfEquals == expectedTextMatchExpression.length - 1) {
            ""
        } else {
            expectedTextMatchExpression.substring(indexOfEquals + 1)
        }

        return textMatcher.matches(expressionWithoutPrefix, actualText)
    }

}
