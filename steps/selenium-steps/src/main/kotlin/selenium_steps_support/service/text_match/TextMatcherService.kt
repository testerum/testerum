package selenium_steps_support.service.text_match

import selenium_steps_support.service.text_match.matchers.*

object TextMatcherService {

    // todo: make these strategies pluggable
    private val prefixToTextMatcherMap: Map<String, TextMatcher> = mapOf(
            "exact"                   to ExactTextMatcher,
            "contains"                to ContainsCaseSensitiveTextMatcher,
            "containsCaseSensitive"   to ContainsCaseSensitiveTextMatcher,
            "containsCaseInsensitive" to ContainsCaseInsensitiveTextMatcher,
            "regex"                   to RegexTextMatcher // todo: global Regex cache (with limited number of entries to not run out of memory)
    )

    private val validPrefixes: List<String> = prefixToTextMatcherMap.keys.map { it + "=" }

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