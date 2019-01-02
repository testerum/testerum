package com.testerum.runner.cmdline.output_format.marshaller.properties

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.hamcrest.CoreMatchers.`is` as Is

class RunnerPropertiesSerializerTest {

    @Test
    fun `should serialize empty map`() {
        assertThat(
                RunnerPropertiesSerializer.serialize(emptyMap()),
                Is(equalTo(""))
        )
    }

    @Test
    fun `should serialize a single property`() {
        assertThat(
                RunnerPropertiesSerializer.serialize(
                        linkedMapOf(
                                "key" to "value"
                        )
                ),
                Is(equalTo("key=value"))
        )
    }

    @Test
    fun `should not add equals for empty values`() {
        assertThat(
                RunnerPropertiesSerializer.serialize(
                        linkedMapOf(
                                "key" to ""
                        )
                ),
                Is(equalTo("key"))
        )
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
                ),
                Is(equalTo("one=1,two=2,thirty=30,key without value"))
        )
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
                ),
                Is(equalTo("equation=1+2\\=3,greeting=Hello\\, world!,all together now=equals\\=/comma\\,/works"))
        )
    }

}
