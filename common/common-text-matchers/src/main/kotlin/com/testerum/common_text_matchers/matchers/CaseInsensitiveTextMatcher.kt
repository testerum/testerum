package com.testerum.common_text_matchers.matchers

object CaseInsensitiveTextMatcher : TextMatcher {

    override fun matches(expectedText: String, actualText: String): Boolean {
        return expectedText.equals(actualText, ignoreCase = true)
    }

}
