package com.testerum.settings.reference_parser

import com.testerum.settings.reference_parser.model.FixedValuePart
import com.testerum.settings.reference_parser.model.ReferenceValuePart
import com.testerum.settings.reference_parser.model.ValuePart

/**
 * Parses references within setting values: "some {{good}} stuff" will be parsed to a list with 3 items
 */
object SettingValueReferenceParser {

    fun parse(value: String): List<ValuePart> {
        if (value.isEmpty()) {
            return emptyList()
        }

        val result = ArrayList<ValuePart>()

        val buffer = ParsingBuffer(value)

        var insideReference = false
        while (!buffer.done()) {
            when {
                buffer.matches("{{") -> {
                    if (insideReference) {
                        buffer.advance(2)
                    } else {
                        insideReference = true
                        val markText = buffer.getMarkText()
                        if (markText.isNotEmpty()) {
                            result.add(
                                    FixedValuePart(markText)
                            )
                        }

                        buffer.advance(2)
                        buffer.mark()
                    }
                }
                buffer.matches("\\}}") -> {
                    buffer.ignore(1)
                    buffer.advance(2)
                }
                buffer.matches("}}") -> {
                    insideReference = false
                    val trimmedMarkText = buffer.getTrimmedMarkText()
                    if (trimmedMarkText.isNotEmpty()) {
                        result.add(
                                ReferenceValuePart(trimmedMarkText)
                        )
                    }

                    buffer.advance(2)
                    buffer.mark()
                }
                else -> buffer.advance(1)
            }
        }

        if (insideReference) {
            throw IllegalArgumentException("value [$value] contain an unterminated reference (not all '{{' are followed by '}}')")
        }

        // append the last part
        val markText = buffer.getMarkText()
        if (markText.isNotEmpty()) {
            result.add(
                    FixedValuePart(markText)
            )
        }

        return result
    }

    private class ParsingBuffer(private val text: String) {
        private var index = 0
        private var markText = StringBuilder()

        fun done(): Boolean = (index >= text.length)

        fun advance(count: Int) {
            for (i in index until index + count) {
                markText.append(text[i])
            }

            index += count
        }

        fun ignore(count: Int) {
            index += count
        }

        fun matches(searchText: String): Boolean {
            if (searchText.length > remainingCharactersCount()) {
                return false
            }

            for (searchIndex in 0 until searchText.length) {
                val textChar = text[index + searchIndex]
                val searchChar = searchText[searchIndex]

                if (textChar != searchChar) {
                    return false
                }
            }

            return true
        }

        fun mark() {
            markText = StringBuilder()
        }

        fun getMarkText(): String {
            return markText.toString()
        }

        fun getTrimmedMarkText(): String {
            var start = 0
            while ((start < markText.length) && markText[start].isWhitespace()) {
                start++
            }

            var end = markText.length - 1
            while ((end >= start) && markText[end].isWhitespace()) {
                end--
            }

            return markText.substring(start, end + 1)
        }

        private fun remainingCharactersCount() = (text.length - index)

        private fun Char.isWhitespace() : Boolean {
            return (this == ' ')
                    || (this == '\t')
                    || (this == '\n')
                    || (this == '\r')
        }
    }

}
