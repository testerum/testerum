package com.testerum.runner.cmdline.report_type.marshaller.properties

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RunnerPropertiesParserTest {

    @Test
    fun `should parse empty properties`() {
        val properties = RunnerPropertiesParser.parse("")

        assertThat(properties).isEmpty()
    }

    @Test
    fun `should parse a single property`() {
        val properties = RunnerPropertiesParser.parse("key=value")

        assertThat(
            LinkedHashMap(properties)
        ).isEqualTo(
            linkedMapOf(
                "key" to "value"
            )
        )
    }

    @Test
    fun `should parse multiple properties`() {
        val properties = RunnerPropertiesParser.parse("one=1,two=2,thirty=30,key without value")

        assertThat(
            LinkedHashMap(properties)
        ).isEqualTo(
            linkedMapOf(
                "one" to "1",
                "two" to "2",
                "thirty" to "30",
                "key without value" to ""
            )
        )
    }

    @Test
    fun `should parse escaped properties`() {
        val properties = RunnerPropertiesParser.parse("equation=1+2\\=3,greeting=Hello\\, world!,all together now=equals\\=/comma\\,/works")

        assertThat(
            LinkedHashMap(properties)
        ).isEqualTo(
            linkedMapOf(
                "equation" to "1+2=3",
                "greeting" to "Hello, world!",
                "all together now" to "equals=/comma,/works"
            )
        )
    }

}
