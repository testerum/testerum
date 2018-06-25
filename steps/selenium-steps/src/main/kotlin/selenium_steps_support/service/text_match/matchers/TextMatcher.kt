package selenium_steps_support.service.text_match.matchers

interface TextMatcher {

    fun matches(expectedText: String, actualText: String): Boolean

}
