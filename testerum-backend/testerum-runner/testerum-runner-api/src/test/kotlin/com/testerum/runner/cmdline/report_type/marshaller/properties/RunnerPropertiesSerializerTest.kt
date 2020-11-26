package com.testerum.runner.cmdline.report_type.marshaller.properties

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RunnerPropertiesSerializerTest {

    @Test
    fun `should serialize empty map`() {
        assertThat(
            RunnerPropertiesSerializer.serialize(emptyMap())
        ).isEqualTo("")
    }

    @Test
    fun `should serialize a single property`() {
        assertThat(
            RunnerPropertiesSerializer.serialize(
                linkedMapOf(
                    "key" to "value"
                )
            )
        ).isEqualTo("key=value")
    }

    @Test
    fun `should not add equals for empty values`() {
        assertThat(
            RunnerPropertiesSerializer.serialize(
                linkedMapOf(
                    "key" to ""
                )
            )
        ).isEqualTo("key")
    }

    @Test
    fun `should serialize multiple properties`() {
        assertThat(
            RunnerPropertiesSerializer.serialize(
                linkedMapOf(
                    "one" to "1",
                    "two" to "2",
                    "thirty" to "30",
                    "key without value" to ""
                )
            )
        ).isEqualTo("one=1,two=2,thirty=30,key without value")
    }

    @Test
    fun `should properly escape keys and values`() {
        assertThat(
            RunnerPropertiesSerializer.serialize(
                linkedMapOf(
                    "equation" to "1+2=3",
                    "greeting" to "Hello, world!",
                    "all together now" to "equals=/comma,/works"
                )
            )
        ).isEqualTo("equation=1+2\\=3,greeting=Hello\\, world!,all together now=equals\\=/comma\\,/works")
    }

}
