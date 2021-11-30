package com.testerum.common_text_matchers.matchers

object RegexTextMatcher : TextMatcher {

    // todo: global Regex cache (with limited number of entries to not run out of memory)

    override fun matches(expectedText: String, actualText: String): Boolean {
        return actualText.matches(
                Regex(expectedText)
        )
    }

}
