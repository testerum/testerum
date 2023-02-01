package selenium_steps_support.service.text_match.matchers

object RegexTextMatcher : TextMatcher {

    // todo: global Regex cache (with limited number of entries to not run out of memory)

    override fun matches(expectedText: String, actualText: String): Boolean {
        return actualText.matches(
                Regex(expectedText)
        )
    }

}
