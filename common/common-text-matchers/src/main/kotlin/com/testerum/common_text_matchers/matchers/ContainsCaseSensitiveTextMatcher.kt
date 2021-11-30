package com.testerum.common_text_matchers.matchers

object ContainsCaseSensitiveTextMatcher : TextMatcher {

    override fun matches(expectedText: String, actualText: String): Boolean {
        return actualText.contains(expectedText, ignoreCase = false)
    }

}
