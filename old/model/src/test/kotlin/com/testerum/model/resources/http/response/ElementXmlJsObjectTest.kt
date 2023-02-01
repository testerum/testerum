package com.testerum.model.resources.http.response

import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.model.resources.http.response.xml_body.RootElementXmlJsObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ElementXmlJsObjectTest {

    private fun evaluateExpression(expression: String, rootElementXmlJsObject: RootElementXmlJsObject): Any? =
        ExpressionEvaluator.evaluate(
            expression,
            mapOf("xmlBody" to rootElementXmlJsObject)
        )

    @Nested
    @DisplayName("root element")
    inner class RootElement {

        @Test
        fun `use xpath to get attribute`() {
            val rootElementXmlJsObject = RootElementXmlJsObject(
                """
                    <bookstore>
                        <book>
                            <title lang="en" />
                        </book>
                      
                        <book>
                            <title lang="nl" />
                        </book>
                    </bookstore>
                """.trimIndent().toByteArray()
            )

            assertThat(evaluateExpression("xmlBody.xpath('/*/book[1]/title/@lang')", rootElementXmlJsObject))
                .isEqualTo("en")

            assertThat(evaluateExpression("xmlBody.xpath('/*/book[2]/title/@lang')", rootElementXmlJsObject))
                .isEqualTo("nl")
        }

        @Test
        fun `use xpath to get text`() {
            val rootElementXmlJsObject = RootElementXmlJsObject(
                """
                    <bookstore>
                        <book>
                            <title>Atomic Habits</title>
                        </book>
                      
                        <book>
                            <title>Learning XML</title>
                        </book>
                    </bookstore>
                """.trimIndent().toByteArray()
            )

            assertThat(evaluateExpression("xmlBody.xpath('/*/book[1]/title/text()')", rootElementXmlJsObject))
                .isEqualTo("Atomic Habits")

            assertThat(evaluateExpression("xmlBody.xpath('/*/book[2]/title/text()')", rootElementXmlJsObject))
                .isEqualTo("Learning XML")
        }

        @Test
        fun `use xpath to get multiple elements`() {
            val rootElementXmlJsObject = RootElementXmlJsObject(
            """
                    <bookstore>
                        <book />
                      
                        <book>
                            super cool
                            <title lang="nl">Learning XML</title>
                            definitely yes
                            <price>39.95</price>
                            it work like this also
                        </book>
                    </bookstore>
                """.trimIndent().toByteArray()
            )

            assertThat(evaluateExpression("xmlBody.xpath('/*/book[2]/text()')", rootElementXmlJsObject))
                .isEqualTo(
                    listOf(
                        "super cool",
                        "definitely yes",
                        "it work like this also"
                    )
                )
        }
    }

    @Nested
    @DisplayName("inner element")
    inner class InnerElement {

        @Test
        fun `get attribute value`() {
            val rootElementXmlJsObject = RootElementXmlJsObject(
                """
                    <bookstore>
                        <book>
                            <title lang="en" />
                        </book>
                      
                        <book>
                            <title lang="nl" />
                        </book>
                    </bookstore>
                """.trimIndent().toByteArray()
            )

            assertThat(evaluateExpression("xmlBody.bookstore.book[0].title.getAttr('lang')", rootElementXmlJsObject))
                .isEqualTo("en")

            assertThat(evaluateExpression("xmlBody.bookstore.book[1].title.getAttr('lang')", rootElementXmlJsObject))
                .isEqualTo("nl")
        }

        @Test
        fun `get text`() {
            val rootElementXmlJsObject = RootElementXmlJsObject(
                """
                    <bookstore>
                        <book />
                      
                        <book>
                            super cool
                            <title lang="nl">Learning XML</title>
                            definitely yes
                            <price>39.95</price>
                            it work like this also
                        </book>
                    </bookstore>
                """.trimIndent().toByteArray()
            )

            assertThat(evaluateExpression("xmlBody.bookstore.book[1].getText()", rootElementXmlJsObject))
                .isEqualTo("super cool definitely yes it work like this also")

            assertThat(evaluateExpression("xmlBody.bookstore.book[1].getText(0)", rootElementXmlJsObject))
                .isEqualTo("super cool")

            assertThat(evaluateExpression("xmlBody.bookstore.book[1].getText(1)", rootElementXmlJsObject))
                .isEqualTo("definitely yes")

            assertThat(evaluateExpression("xmlBody.bookstore.book[1].getText(2)", rootElementXmlJsObject))
                .isEqualTo("it work like this also")
        }

        @Test
        fun `get child`() {
            val rootElementXmlJsObject = RootElementXmlJsObject(
                """
                    <bookstore>
                        <book>
                            <title lang="en">Atomic Habits</title>
                            <price>29.99</price>
                        </book>
                      
                        <book>
                            super cool
                            <title lang="nl">Learning XML</title>
                            definitely yes
                            <price>39.95</price>
                            it work like this also
                        </book>
                        
                        <some-element some-attribute='some value'/>
                        
                        <getAttr>getAttr text</getAttr>
                        <getText>getText text</getText>
                        <getChild>getChild text</getChild>
                        <xpath>xpath text</xpath>
                    </bookstore>
                """.trimIndent().toByteArray()
            )

            assertThat(evaluateExpression("xmlBody.bookstore.getChild('book')[0].getChild('title').getAttr('lang')", rootElementXmlJsObject))
                .isEqualTo("en")

            assertThat(evaluateExpression("xmlBody.bookstore['some-element'].getAttr('some-attribute')", rootElementXmlJsObject))
                .isEqualTo("some value")

            assertThat(evaluateExpression("xmlBody.bookstore.getChild('getAttr').getText()", rootElementXmlJsObject))
                .isEqualTo("getAttr text")
            assertThat(evaluateExpression("xmlBody.bookstore.getChild('getText').getText()", rootElementXmlJsObject))
                .isEqualTo("getText text")
            assertThat(evaluateExpression("xmlBody.bookstore.getChild('getChild').getText()", rootElementXmlJsObject))
                .isEqualTo("getChild text")
            assertThat(evaluateExpression("xmlBody.bookstore.getChild('xpath').getText()", rootElementXmlJsObject))
                .isEqualTo("xpath text")
        }

        @Test
        fun `use xpath to get attribute`() {
            val rootElementXmlJsObject = RootElementXmlJsObject(
                """
                    <bookstore>
                        <book>
                            <title lang="en" />
                        </book>
                    </bookstore>
                """.trimIndent().toByteArray()
            )

            assertThat(evaluateExpression("xmlBody.bookstore.book.xpath('title/@lang')", rootElementXmlJsObject))
                .isEqualTo("en")
        }

        @Test
        fun `use xpath to get text`() {
            val rootElementXmlJsObject = RootElementXmlJsObject(
                """
                    <bookstore>
                        <book>
                            <title>Atomic Habits</title>
                        </book>
                    </bookstore>
                """.trimIndent().toByteArray()
            )

            assertThat(evaluateExpression("xmlBody.bookstore.book.xpath('title/text()')", rootElementXmlJsObject))
                .isEqualTo("Atomic Habits")
        }

        @Test
        fun `use xpath to get multiple elements`() {
            val rootElementXmlJsObject = RootElementXmlJsObject(
                """
                    <bookstore>
                        <book>
                            <author>John Doe</author>
                            <author>Jane Doe</author>
                        </book>
                    </bookstore>
                """.trimIndent().toByteArray()
            )

            assertThat(evaluateExpression("xmlBody.bookstore.book.xpath('author/text()')", rootElementXmlJsObject))
                .isEqualTo(
                    listOf(
                        "John Doe",
                        "Jane Doe",
                    )
                )
        }

    }

}
