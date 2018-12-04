package com.testerum.runner.cmdline

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.anEmptyMap
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.hamcrest.CoreMatchers.`is` as Is

class OutputFormatParserTest {

    @Test
    fun `should give an informative error message when the output doesn't exist`() {
        val exception = assertThrows<IllegalArgumentException> {
            OutputFormatParser.parse("BLAH")
        }

        assertThat(exception.message, Is(equalTo("there is no output format [BLAH]; valid values are: [TREE], [JSON_EVENTS]")))
    }

    @Test
    fun `should parse output format without properties`() {
        val (outputFormat, properties) = OutputFormatParser.parse("JSON_EVENTS")

        assertThat(outputFormat, Is(equalTo(OutputFormat.JSON_EVENTS)))
        assertThat(properties, Is(anEmptyMap()))
    }

    @Test
    fun `should parse output format with empty properties`() {
        val (outputFormat, properties) = OutputFormatParser.parse("JSON_EVENTS:")

        assertThat(outputFormat, Is(equalTo(OutputFormat.JSON_EVENTS)))
        assertThat(properties, Is(anEmptyMap()))
    }

    @Test
    fun `should parse output format with a single property`() {
        val (outputFormat, properties) = OutputFormatParser.parse("JSON_EVENTS:key=value")

        assertThat(outputFormat, Is(equalTo(OutputFormat.JSON_EVENTS)))
        assertThat(
                LinkedHashMap(properties),
                equalTo(
                        linkedMapOf(
                                "key" to "value"
                        )
                )
        )
    }

    @Test
    fun `should parse output format with a multiple properties`() {
        val (outputFormat, properties) = OutputFormatParser.parse("JSON_EVENTS:one=1,two=2,thirty=30,key without value")

        assertThat(outputFormat, Is(equalTo(OutputFormat.JSON_EVENTS)))
        assertThat(
                LinkedHashMap(properties),
                equalTo(
                        linkedMapOf(
                                "one"               to "1",
                                "two"               to "2",
                                "thirty"            to "30",
                                "key without value" to ""
                        )
                )
        )
    }

    @Test
    fun `should parse output format with escaped properties`() {
        val (outputFormat, properties) = OutputFormatParser.parse("JSON_EVENTS:equation=1+2\\=3,greeting=Hello\\, world!,all together now=equals\\=/comma\\,/works")

        assertThat(outputFormat, Is(equalTo(OutputFormat.JSON_EVENTS)))
        assertThat(
                LinkedHashMap(properties),
                equalTo(
                        linkedMapOf(
                                "equation"         to "1+2=3",
                                "greeting"         to "Hello, world!",
                                "all together now" to "equals=/comma,/works"
                        )
                )
        )
    }

}