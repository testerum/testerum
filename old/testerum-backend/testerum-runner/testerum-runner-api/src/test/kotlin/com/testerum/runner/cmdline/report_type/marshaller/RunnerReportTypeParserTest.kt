package com.testerum.runner.cmdline.report_type.marshaller

import com.testerum.runner.cmdline.report_type.RunnerReportType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RunnerReportTypeParserTest {

    @Test
    fun `should give an informative error message when the output doesn't exist`() {
        val exception = assertThrows<IllegalArgumentException> {
            RunnerReportTypeParser.parse("BLAH")
        }

        assertThat(exception.message)
            .isEqualTo("there is no report type [BLAH]; valid values are: ${RunnerReportType.VALID_VALUES}")
    }

    @Test
    fun `should parse report type without properties`() {
        val (reportType, properties) = RunnerReportTypeParser.parse("JSON_EVENTS")

        assertThat(reportType).isEqualTo(RunnerReportType.JSON_EVENTS)
        assertThat(properties).isEmpty()
    }

    @Test
    fun `should parse report type with empty properties`() {
        val (reportType, properties) = RunnerReportTypeParser.parse("JSON_EVENTS:")

        assertThat(reportType).isEqualTo(RunnerReportType.JSON_EVENTS)
        assertThat(properties).isEmpty()
    }

    @Test
    fun `should parse report type with a single property`() {
        val (reportType, properties) = RunnerReportTypeParser.parse("JSON_EVENTS:key=value")

        assertThat(reportType).isEqualTo(RunnerReportType.JSON_EVENTS)
        assertThat(
            LinkedHashMap(properties)
        ).isEqualTo(
            linkedMapOf(
                "key" to "value"
            )
        )
    }

    @Test
    fun `should parse report type with a multiple properties`() {
        val (reportType, properties) = RunnerReportTypeParser.parse("JSON_EVENTS:one=1,two=2,thirty=30,key without value")

        assertThat(reportType).isEqualTo(RunnerReportType.JSON_EVENTS)
        assertThat(
            LinkedHashMap(properties)
        ).isEqualTo(
            linkedMapOf(
                "one" to "1",
                "two" to "2",
                "thirty" to "30",
                "key without value" to ""
            )
        )
    }

    @Test
    fun `should parse report type with escaped properties`() {
        val (reportType, properties) = RunnerReportTypeParser.parse("JSON_EVENTS:equation=1+2\\=3,greeting=Hello\\, world!,all together now=equals\\=/comma\\,/works")

        assertThat(reportType).isEqualTo(RunnerReportType.JSON_EVENTS)
        assertThat(
            LinkedHashMap(properties)
        ).isEqualTo(
            linkedMapOf(
                "equation" to "1+2=3",
                "greeting" to "Hello, world!",
                "all together now" to "equals=/comma,/works"
            )
        )
    }

}
