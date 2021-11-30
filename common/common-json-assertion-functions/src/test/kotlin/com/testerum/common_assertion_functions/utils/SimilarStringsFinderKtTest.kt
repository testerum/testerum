package com.testerum.common_assertion_functions.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SimilarStringsFinderKtTest {

    @Test
    fun `empty list`() {
        assertThat(didYouMean("", listOf<String>()))
            .isEqualTo("")
    }

    @Test
    fun `no match`() {
        assertThat(didYouMean("x", listOf("aaa", "bbb", "ccc")))
            .isEqualTo("")
    }

    @Test
    fun `one match`() {
        assertThat(didYouMean("aax", listOf("aaa", "bbb", "ccc")))
            .isEqualTo("; did you mean [aaa]?")
    }

    @Test
    fun `multiple matches match`() {
        assertThat(didYouMean("00x", listOf("00a", "00b", "00c", "ddd")))
            .isEqualTo("; did you mean one of [00a, 00b, 00c]?")
    }

}
