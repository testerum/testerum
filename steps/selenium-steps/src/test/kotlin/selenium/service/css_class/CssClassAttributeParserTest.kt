package selenium.service.css_class

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import selenium_steps_support.service.css_class.CssClassAttributeParser.parse

class CssClassAttributeParserTest {

    @Test
    fun `empty list`() {
        assertThat(parse(""))
            .isEmpty()
    }

    @Test
    fun `all-space list`() {
        assertThat(parse(" \t \n \t\t \r  \u000C \t   "))
            .isEmpty()
    }

    @Test
    fun `single item, simple`() {
        assertThat(parse("a-class-name"))
            .isEqualTo(setOf("a-class-name"))
    }

    @Test
    fun `single item, surrounded by spaces`() {
        assertThat(parse("\t   \na-class-name\u000c\t  \n  "))
            .isEqualTo(setOf("a-class-name"))
    }

    @Test
    fun `multiple items`() {
        assertThat(parse("\t   \nclass-name-1\u000c\t  class-name-2  \u000c\t  class-name-3 \n\t\t  "))
            .isEqualTo(linkedSetOf("class-name-1", "class-name-2", "class-name-3") as Set<String>)
    }

}
