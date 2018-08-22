package com.testerum.test_file_format.testdef.properties

import com.testerum.common.parsing.ParserFactory
import com.testerum.common.parsing.util.CommonScanners
import com.testerum.common.parsing.util.CommonScanners.optionalWhitespace
import org.jparsec.Parser
import org.jparsec.Parsers.or
import org.jparsec.Parsers.sequence
import org.jparsec.Scanners.string

object FileTestDefPropertiesParserFactory : ParserFactory<FileTestDefProperties> {

    override fun createParser(): Parser<FileTestDefProperties> = testProperties()

    fun testProperties(): Parser<FileTestDefProperties> {
        return sequence(
                string("test-properties"),
                optionalWhitespace(),
                string("="),
                optionalWhitespace(),
                testPropertiesWithAngleBrackets()
        ) { _, _, _, _, tags -> tags }
    }

    private fun testPropertiesWithAngleBrackets(): Parser<FileTestDefProperties> {
        return sequence(
                string("<<"),
                CommonScanners.optionalWhitespaceOrNewLines(),
                propertiesList(),
                CommonScanners.optionalWhitespaceOrNewLines(),
                string(">>"),
                CommonScanners.optionalWhitespaceOrNewLines()
        ) { _, _, properties, _, _, _ -> properties }
    }

    private fun propertiesList(): Parser<FileTestDefProperties> {
        return sequence(
                CommonScanners.optionalWhitespaceOrNewLines(),
                property(),
                CommonScanners.optionalWhitespaceOrNewLines(),
                sequence(
                        string(","),
                        CommonScanners.optionalWhitespaceOrNewLines(),
                        property(),
                        CommonScanners.optionalWhitespaceOrNewLines()
                ) { _, _, tag, _ -> tag }.many()
        ) { _, firstProperty, _, restOfProperties -> createProperties(firstProperty, restOfProperties) }
    }

    private fun property(): Parser<String?> {
        return or(
                string("manual").source(),
                string("disabled").source()
        ).optional(null)
        .map { text -> text }
    }

    private fun createProperties(firstProperty: String?,
                                 restOfProperties: List<String?>): FileTestDefProperties {
        val properties = mutableSetOf<String>()

        if (firstProperty != null) {
            properties.add(firstProperty)
        }

        for (restOfProperty in restOfProperties) {
            if (restOfProperty != null) {
                properties.add(restOfProperty)
            }
        }

        return FileTestDefProperties(
                isManual = properties.contains("manual"),
                isDisabled = properties.contains("disabled")
        )
    }

}