package selenium_steps_support.service.text_match.matchers

object ContainsCaseInsensitiveTextMatcher : TextMatcher {

    override fun matches(expectedText: String, actualText: String): Boolean {
        return actualText.contains(expectedText, ignoreCase = true)
    }

}