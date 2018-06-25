package selenium_steps_support.service.text_match.matchers

object RegexTextMatcher : TextMatcher {

    override fun matches(expectedText: String, actualText: String): Boolean {
        return actualText.matches(
                Regex(expectedText)
        )
    }

}