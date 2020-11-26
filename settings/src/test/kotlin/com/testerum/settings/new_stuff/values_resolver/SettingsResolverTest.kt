package com.testerum.settings.new_stuff.values_resolver

import com.testerum.settings.reference_resolver.CyclicReferenceException
import com.testerum.settings.reference_resolver.SettingsResolver.resolve
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SettingsResolverTest {

    @Test
    fun `cyclic reference`() {
        val exception = assertThrows<CyclicReferenceException> {
            resolve(
                mapOf(
                    "key-0" to "value-0",
                    "key-1" to "{{key-2}}",
                    "key-2" to "{{key-3}}",
                    "key-3" to "{{key-1}}",
                    "key-4" to "value-4"
                )
            )
        }

        assertThat(exception.message)
            .isEqualTo("the following keys refer to each other, forming a cycle: [key-1, key-2, key-3]")
    }

    @Test
    fun `empty map`() {
        assertThat(
            resolve(
                emptyMap()
            )
        ).isEmpty()
    }

    @Test
    fun `unresolved reference`() {
        assertThat(
            resolve(
                mapOf(
                    "key-1" to "{{key-2}}",
                    "key-2" to "{{nope}}"
                )
            )
        ).isEqualTo(
            mapOf<String, String?>(
                "key-1" to "{{nope}}",
                "key-2" to "{{nope}}"
            )
        )
    }

    @Test
    fun `no references`() {
        assertThat(
            resolve(
                mapOf(
                    "key-1" to "value-1",
                    "key-2" to "value-2",
                    "key-3" to "value-3"
                )
            )
        ).isEqualTo(
            mapOf<String, String?>(
                "key-1" to "value-1",
                "key-2" to "value-2",
                "key-3" to "value-3"
            )
        )
    }

    @Test
    fun `backward reference`() {
        assertThat(
            resolve(
                mapOf(
                    "key-1" to "value-1",
                    "key-2" to "value-2 / {{key-1}}",
                    "key-3" to "value-3 / {{key-2}}"
                )
            )
        ).isEqualTo(
            mapOf<String, String?>(
                "key-1" to "value-1",
                "key-2" to "value-2 / value-1",
                "key-3" to "value-3 / value-2 / value-1"
            )
        )
    }

    @Test
    fun `forward reference`() {
        assertThat(
            resolve(
                mapOf(
                    "key-1" to "value-1 / {{key-2}}",
                    "key-2" to "value-2 / {{key-3}}",
                    "key-3" to "value-3"
                )
            )
        ).isEqualTo(
            mapOf<String, String?>(
                "key-1" to "value-1 / value-2 / value-3",
                "key-2" to "value-2 / value-3",
                "key-3" to "value-3"
            )
        )
    }

    @Test
    fun `whitespace is ignored in reference`() {
        assertThat(
            resolve(
                mapOf(
                    "key-1" to "value-1",
                    "key-2" to "value-2 / {{   key-1 }}"
                )
            )
        ).isEqualTo(
            mapOf<String, String?>(
                "key-1" to "value-1",
                "key-2" to "value-2 / value-1"
            )
        )
    }

}
