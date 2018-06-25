package selenium.service.keys_expression_parser

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.parsing.executer.ParserExecuterException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.openqa.selenium.Keys
import selenium_steps_support.service.keys_expression_parser.KeysExpression
import selenium_steps_support.service.keys_expression_parser.KeysExpressionParserFactory

@Disabled
class KeysExpressionParserFactoryTest {

    private val parser = ParserExecuter(
            KeysExpressionParserFactory.createParser()
    )


    @Test
    fun `test empty`() {
        assertThat(
                parser.parse(""),
                equalTo(
                        KeysExpression(emptyList())
                )
        )
    }

    @Test
    fun `test only simple text`() {
        assertThat(
                parser.parse("abcd"),
                equalTo(
                        KeysExpression(listOf("abcd"))
                )
        )
    }

    @Test
    fun `test simple text mixed with special keys`() {
        assertThat(
                parser.parse("abcd{BACK_SPACE}D"),
                equalTo(
                        KeysExpression(listOf("abcd", Keys.BACK_SPACE.toString(), "D"))
                )
        )
    }

    @Test
    fun `test with chords`() {
        assertThat(
                parser.parse("a bad({CONTROL}{SHIFT}{}),D"),
                equalTo(
                        KeysExpression(listOf("abcd", Keys.BACK_SPACE.toString(), "D"))
                )
        )
    }

    // todo: test for chords

    @Test
    fun `test unrecognized special key`() {
        val exception: ParserExecuterException = Assertions.assertThrows(ParserExecuterException::class.java) {
            parser.parse("{UNRECOGNIZED_SPECIAL_KEY}")
        }

        assertThat(
                exception.message,
                equalTo(
                        "failed to parse:\n" +
                                "{UNRECOGNIZED_SPECIAL_KEY}\n" +
                                "^--- ERROR at line 1, column 1: blah, blah, fix this test"
                )
        )
    }

}