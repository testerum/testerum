package selenium_steps_support.service.text_match.matchers

object CaseInsensitiveTextMatcher : TextMatcher {

    override fun matches(expectedText: String, actualText: String): Boolean {
        return expectedText.equals(actualText, ignoreCase = true)
    }

}
