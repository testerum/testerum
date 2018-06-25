package selenium.service.css_class

import selenium_steps_support.service.css_class.CssClassAttributeParser.parse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class CssClassAttributeParserTest {

    @Test
    fun `empty list`() {
        assertThat(
                parse(""),
                empty()
        )
    }

    @Test
    fun `all-space list`() {
        assertThat(
                parse(" \t \n \t\t \r  \u000C \t   "),
                empty()
        )
    }

    @Test
    fun `single item, simple`() {
        assertThat(
                parse("a-class-name"),
                equalTo(setOf("a-class-name"))
        )
    }

    @Test
    fun `single item, surrounded by spaces`() {
        assertThat(
                parse("\t   \na-class-name\u000c\t  \n  "),
                equalTo(setOf("a-class-name"))
        )
    }

    @Test
    fun `multiple items`() {
        assertThat(
                parse("\t   \nclass-name-1\u000c\t  class-name-2  \u000c\t  class-name-3 \n\t\t  "),
                equalTo(linkedSetOf("class-name-1", "class-name-2", "class-name-3") as Set<String>)
        )
    }

}