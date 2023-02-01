package com.testerum.test_file_format.common.step_call.part.arg_part

import com.testerum.test_file_format.test_util.SerializerTestRunner
import org.junit.jupiter.api.Test

class FileArgPartSerializerTest {

    private val testRunner = SerializerTestRunner(
            FileArgPartSerializer,
            FileArgPartParserFactory.argPart()
    )

    @Test
    fun `simple text`() {
        testRunner.execute(
                original = FileTextArgPart("some text"),
                indentLevel = 0,
                expected = """some text"""
        )
    }

    @Test
    fun `simple expression`() {
        testRunner.execute(
                original = FileExpressionArgPart("someExpr"),
                indentLevel = 0,
                expected = """{{someExpr}}"""
        )
    }

    @Test
    fun `text with escaped expression start`() {
        testRunner.execute(
                original = FileTextArgPart("left {{ right"),
                indentLevel = 0,
                expected = """left \{{ right"""
        )
    }

    @Test
    fun `expression with escaped expression start`() {
        testRunner.execute(
                original = FileExpressionArgPart("left }} right"),
                indentLevel = 0,
                expected = """{{left \}} right}}"""
        )
    }

}
