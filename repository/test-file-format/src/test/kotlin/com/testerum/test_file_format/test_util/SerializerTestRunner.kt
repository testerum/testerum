package com.testerum.test_file_format.test_util

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.serializing.Serializer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.jparsec.Parser

class SerializerTestRunner<T>(private val serializer: Serializer<T>,
                              parser: Parser<T>) {

    private val executer = ParserExecuter(parser)

    fun execute(original: T,
                indentLevel: Int,
                expected: String) {
        val serialized = serializer.serializeToString(original, indentLevel)

        assertThat(
                serialized,
                equalTo(expected)
        )
        assertThat(
                executer.parse(serialized),
                equalTo(original)
        )
    }

}