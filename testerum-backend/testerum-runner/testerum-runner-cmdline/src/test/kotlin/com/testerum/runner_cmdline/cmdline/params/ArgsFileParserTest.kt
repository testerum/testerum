package com.testerum.runner_cmdline.cmdline.params

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ArgsFileParserTest {

    @Test
    fun `test complex`() {
        val fileContent = "" +
            "\n" +
            "--repository-directory\n" +
            "/path that/can even/contain spaces\n" +
            "slash and the end of the line\\\\\n" +
            "slash in the \\\\ middle of the line\n" +
            "--setting\n" +
            "MESSAGE=This is a message split on multiple lines:\\\n" +
            "    * one\\\n" +
            "    * 2\\\n" +
            "    * III\\\n" +
            "    * 4th"
        val args = ArgsFileParser.parseArgsFile(fileContent)
        val endTime = System.nanoTime()

        assertThat(args).hasSize(7)
        assertThat(args[0]).isEmpty()
        assertThat(args[1]).isEqualTo("--repository-directory")
        assertThat(args[2]).isEqualTo("/path that/can even/contain spaces")
        assertThat(args[3]).isEqualTo("slash and the end of the line\\")
        assertThat(args[4]).isEqualTo("slash in the \\ middle of the line")
        assertThat(args[5]).isEqualTo("--setting")
        assertThat(args[6]).isEqualTo(
            "MESSAGE=This is a message split on multiple lines:\n" +
                "    * one\n" +
                "    * 2\n" +
                "    * III\n" +
                "    * 4th"
        )
    }

    @Test
    fun `test escaped backslash`() {
        val fileContent = "\\\\"
        val args = ArgsFileParser.parseArgsFile(fileContent)

        assertThat(args).hasSize(1)
        assertThat(args[0]).isEqualTo("\\")
    }

    @Test
    fun `test does not end in newline`() {
        val fileContent = "line 1\n" +
            "line 2\n" +
            "line 3"
        val args = ArgsFileParser.parseArgsFile(fileContent)

        assertThat(args).hasSize(3)
        assertThat(args[0]).isEqualTo("line 1")
        assertThat(args[1]).isEqualTo("line 2")
        assertThat(args[2]).isEqualTo("line 3")
    }
}
