package com.testerum.settings.new_stuff.values_resolver.parser

import com.testerum.settings.reference_parser.SettingValueReferenceParser
import com.testerum.settings.reference_parser.model.FixedValuePart
import com.testerum.settings.reference_parser.model.ReferenceValuePart
import com.testerum.settings.reference_parser.model.ValuePart
import org.assertj.core.api.Assertions.assertThat
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

        assertThat(exception.message)
            .isEqualTo("value [stuff {{goodRef}} another {{ badRef] contain an unterminated reference (not all '{{' are followed by '}}')")
    }

    @Test
    fun `empty value`() {
        assertThat(parser.parse(""))
            .isEmpty()
    }

    @Test
    fun `fixed value`() {
        assertThat(parser.parse("some value"))
            .isEqualTo(
                listOf<ValuePart>(
                    FixedValuePart("some value")
                )
            )
    }

    @Test
    fun `just reference`() {
        assertThat(parser.parse("{{ref}}"))
            .isEqualTo(
                listOf<ValuePart>(
                    ReferenceValuePart("ref")
                )
            )
    }

    @Test
    fun `just reference, whitespace ignored`() {
        assertThat(parser.parse("{{     ref  }}"))
            .isEqualTo(
                listOf<ValuePart>(
                    ReferenceValuePart("ref")
                )
            )
    }

    @Test
    fun `single reference in the middle`() {
        assertThat(parser.parse("some {{ref}} value"))
            .isEqualTo(
                listOf(
                    FixedValuePart("some "),
                    ReferenceValuePart("ref"),
                    FixedValuePart(" value")
                )
            )
    }

    @Test
    fun `single reference at the beginning`() {
        assertThat(parser.parse("{{ref}} some value"))
            .isEqualTo(
                listOf(
                    ReferenceValuePart("ref"),
                    FixedValuePart(" some value")
                )
            )
    }

    @Test
    fun `single reference at the end`() {
        assertThat(parser.parse("some value {{ref}}"))
            .isEqualTo(
                listOf(
                    FixedValuePart("some value "),
                    ReferenceValuePart("ref")
                )
            )
    }

    @Test
    fun `multiple references, complex case`() {
        assertThat(parser.parse("{{ start }} some {{leftMiddle    }}{{    rightMiddle }} value {{ end }}"))
            .isEqualTo(
                listOf(
                    ReferenceValuePart("start"),
                    FixedValuePart(" some "),
                    ReferenceValuePart("leftMiddle"),
                    ReferenceValuePart("rightMiddle"),
                    FixedValuePart(" value "),
                    ReferenceValuePart("end")
                )
            )
    }

    @Test
    fun `escaping - start doesn't need to be escaped`() {
        assertThat(parser.parse("some value {{ ref{{erence }}"))
            .isEqualTo(
                listOf(
                    FixedValuePart("some value "),
                    ReferenceValuePart("ref{{erence")
                )
            )
    }

    @Test
    fun `escaping - end can be escaped`() {
        assertThat(parser.parse("some value {{ ref\\}}erence }}"))
            .isEqualTo(
                listOf(
                    FixedValuePart("some value "),
                    ReferenceValuePart("ref}}erence")
                )
            )
    }

}
