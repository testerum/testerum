package com.testerum.test_file_format.common.tags

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonPatterns.NEWLINE
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespaceOrNewLines
import org.jparsec.Parser
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string
import org.jparsec.pattern.Patterns

object FileTagsParserFactory : ParserFactory<List<String>> {

    override fun createParser(): Parser<List<String>> = tags()

    fun tags(): Parser<List<String>> {
        return sequence(
                string("tags"),
                optionalWhitespace(),
                string("="),
                optionalWhitespace(),
                tagsWithAngleBrackets()
        ) { _, _, _, _, tags -> tags }
    }

    private fun tagsWithAngleBrackets(): Parser<List<String>> {
        return sequence(
                string("<<"),
                optionalWhitespaceOrNewLines(),
                tagsList().optional(emptyList()),
                optionalWhitespaceOrNewLines(),
                string(">>"),
                optionalWhitespaceOrNewLines()
        ) { _, _, tags, _, _, _ -> tags }
    }

    private fun tagsList(): Parser<List<String>> {
        return sequence(
                optionalWhitespaceOrNewLines(),
                tag(),
                optionalWhitespaceOrNewLines(),
                sequence(
                        string(","),
                        optionalWhitespaceOrNewLines(),
                        tag(),
                        optionalWhitespaceOrNewLines()
                ) { _, _, tag, _ -> tag }.many()
        ) { _, firstTag, _, restOfTags -> createTagLists(firstTag, restOfTags) }
    }

    private fun tag(): Parser<String> {
        return Patterns.and(Patterns.notString(","), Patterns.notString(">>"), Patterns.not(NEWLINE))
                .many1()
                .toScanner("tag")
                .source()
                .map { text -> text }
    }

    private fun createTagLists(firstTag: String,
                               restOfTags: List<String>): List<String> {
        val result = mutableListOf<String>()

        result.add(firstTag)
        result.addAll(restOfTags)

        return result
    }

}