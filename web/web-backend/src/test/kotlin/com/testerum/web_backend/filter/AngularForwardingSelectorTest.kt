package com.testerum.web_backend.filter

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class AngularForwardingSelectorTest {

    @Test
    fun `should forward URLs not in ignore list`() {
        test("/features", true)
    }

    @Test
    fun `should NOT forward JavaScript`() {
        test(requestUri = "/main.a5c2013d91821b333732.js"  , expectedShouldForward = false)
        test(requestUri = "/some/other/dir/stuff.js"       , expectedShouldForward = false)
    }

    @Test
    fun `should NOT forward CSS`() {
        test(requestUri = "/styles.c9a855dafa8f592b1a8b.css"               , expectedShouldForward = false)
        test(requestUri = "/some/other/dir/styles.c9a855dafa8f592b1a8b.css", expectedShouldForward = false)
    }

    @Test
    fun `should NOT forward images`() {
        for (extension in listOf(".ico", ".png", ".gif", ".jpg", ".jpeg", ".svg")) {
            test(requestUri = "/favicon$extension"            , expectedShouldForward = false)
            test(requestUri = "/some/other/dir/logo$extension", expectedShouldForward = false)
        }
    }

    @Test
    fun `should NOT forward fonts`() {
        for (extension in listOf(".ttf", ".eot", ".woff", ".woff2")) {
            test(requestUri = "/font$extension"               , expectedShouldForward = false)
            test(requestUri = "/some/other/dir/font$extension", expectedShouldForward = false)
        }
    }

    @Test
    fun `should NOT forward REST web services`() {
        test(requestUri = "/rest/messages"  , expectedShouldForward = false)
        test(requestUri = "/rest/tests/save", expectedShouldForward = false)
    }

    @Test
    fun `should NOT forward version page`() {
        test(requestUri = "/version.html"            , expectedShouldForward = false)
        test(requestUri = "/version.html?param=value", expectedShouldForward = false)
    }

    private fun test(requestUri: String, expectedShouldForward: Boolean) {
        assertThat(
                "requestUri=[$requestUri]",
                AngularForwardingSelector.shouldForward(requestUri),
                equalTo(expectedShouldForward)
        )
    }

}
