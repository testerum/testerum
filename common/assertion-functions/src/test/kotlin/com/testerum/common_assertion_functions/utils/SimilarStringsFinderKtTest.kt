package com.testerum.common_assertion_functions.utils

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class SimilarStringsFinderKtTest {

    @Test
    fun `empty list`() {
        assertThat(
                didYouMean("", listOf<String>()),
                equalTo("")
        )
    }

    @Test
    fun `no match`() {
        assertThat(
                didYouMean("x", listOf("aaa", "bbb", "ccc")),
                equalTo("")
        )
    }

    @Test
    fun `one match`() {
        assertThat(
                didYouMean("aax", listOf("aaa", "bbb", "ccc")),
                equalTo("; did you mean [aaa]?")
        )
    }

    @Test
    fun `multiple matches match`() {
        assertThat(
                didYouMean("00x", listOf("00a", "00b", "00c", "ddd")),
                equalTo("; did you mean one of [00a, 00b, 00c]?")
        )
    }

}