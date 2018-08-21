package com.testerum.settings.new_stuff.values_resolver.parser

import com.testerum.settings.reference_parser.SettingValueReferenceParser
import com.testerum.settings.reference_parser.model.FixedValuePart
import com.testerum.settings.reference_parser.model.ReferenceValuePart
import com.testerum.settings.reference_parser.model.ValuePart
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SettingValueReferenceParserTest {

    companion object {
        private val parser = SettingValueReferenceParser
    }

    @Test
    fun `unterminated reference`() {
        val exception = assertThrows<IllegalArgumentException> {
            parser.parse("stuff {{goodRef}} another {{ badRef")
        }

        assertThat(exception.message, equalTo("value [stuff {{goodRef}} another {{ badRef] contain an unterminated reference (not all '{{' are followed by '}}')"))
    }

    @Test
    fun `empty value`() {
        assertThat(
                parser.parse(""),
                equalTo(emptyList())
        )
    }

    @Test
    fun `fixed value`() {
        assertThat(
                parser.parse("some value"),
                equalTo(
                        listOf<ValuePart>(
                                FixedValuePart("some value")
                        )
                )
        )
    }

    @Test
    fun `just reference`() {
        assertThat(
                parser.parse("{{ref}}"),
                equalTo(
                        listOf<ValuePart>(
                                ReferenceValuePart("ref")
                        )
                )
        )
    }

    @Test
    fun `just reference, whitespace ignored`() {
        assertThat(
                parser.parse("{{     ref  }}"),
                equalTo(
                        listOf<ValuePart>(
                                ReferenceValuePart("ref")
                        )
                )
        )
    }

    @Test
    fun `single reference in the middle`() {
        assertThat(
                parser.parse("some {{ref}} value"),
                equalTo(
                        listOf(
                                FixedValuePart("some "),
                                ReferenceValuePart("ref"),
                                FixedValuePart(" value")
                        )
                )
        )
    }

    @Test
    fun `single reference at the beginning`() {
        assertThat(
                parser.parse("{{ref}} some value"),
                equalTo(
                        listOf(
                                ReferenceValuePart("ref"),
                                FixedValuePart(" some value")
                        )
                )
        )
    }

    @Test
    fun `single reference at the end`() {
        assertThat(
                parser.parse("some value {{ref}}"),
                equalTo(
                        listOf(
                                FixedValuePart("some value "),
                                ReferenceValuePart("ref")
                        )
                )
        )
    }

    @Test
    fun `multiple references, complex case`() {
        assertThat(
                parser.parse("{{ start }} some {{leftMiddle    }}{{    rightMiddle }} value {{ end }}"),
                equalTo(
                        listOf(
                                ReferenceValuePart("start"),
                                FixedValuePart(" some "),
                                ReferenceValuePart("leftMiddle"),
                                ReferenceValuePart("rightMiddle"),
                                FixedValuePart(" value "),
                                ReferenceValuePart("end")
                        )
                )
        )
    }

    @Test
    fun `escaping - start doesn't need to be escaped`() {
        assertThat(
                parser.parse("some value {{ ref{{erence }}"),
                equalTo(
                        listOf(
                                FixedValuePart("some value "),
                                ReferenceValuePart("ref{{erence")
                        )
                )
        )
    }

    @Test
    fun `escaping - end can be escaped`() {
        assertThat(
                parser.parse("some value {{ ref\\}}erence }}"),
                equalTo(
                        listOf(
                                FixedValuePart("some value "),
                                ReferenceValuePart("ref}}erence")
                        )
                )
        )
    }

}
