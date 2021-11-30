package com.testerum.common_text_matchers.matchers

object ExactTextMatcher : TextMatcher {

    override fun matches(expectedText: String, actualText: String): Boolean {
        return expectedText == actualText
    }

}
