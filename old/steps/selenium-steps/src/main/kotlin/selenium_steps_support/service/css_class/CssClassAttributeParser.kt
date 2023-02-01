package selenium_steps_support.service.css_class

import com.google.common.base.Splitter
import java.util.regex.Pattern

object CssClassAttributeParser {

    // parsed according to https://www.w3.org/TR/2011/WD-html5-20110525/elements.html#classes

    private val SPLITTER = Splitter.on(Pattern.compile("""[ \t\r\n\f]+"""))
                                   .omitEmptyStrings()

    fun parse(cssClassAttrValue: String): Set<String> {
        val result = linkedSetOf<String>()

        SPLITTER.split(cssClassAttrValue)
                .forEach { result.add(it) }

        return result
    }

}