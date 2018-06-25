package selenium_steps_support.service.text_match.matchers

object ExactTextMatcher : TextMatcher {

    override fun matches(expectedText: String, actualText: String): Boolean {
        return expectedText == actualText
    }

}