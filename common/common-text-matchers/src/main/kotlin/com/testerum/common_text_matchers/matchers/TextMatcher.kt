package com.testerum.common_text_matchers.matchers

interface TextMatcher {

    fun matches(expectedText: String, actualText: String): Boolean

}
