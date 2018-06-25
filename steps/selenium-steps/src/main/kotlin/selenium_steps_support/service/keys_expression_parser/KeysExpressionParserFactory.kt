package selenium_steps_support.service.keys_expression_parser

import com.testerum.common.parsing.ParserFactory
import org.jparsec.Parser

object KeysExpressionParserFactory : ParserFactory<KeysExpression> {

    override fun createParser(): Parser<KeysExpression> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    private val keysMap: Map<String, String> = run {
//        val keysMap = mutableMapOf<String, String>()
//
//        for (enumConstant in Keys::class.java.enumConstants) {
//            keysMap.put(enumConstant.name, enumConstant.toString())
//        }
//
//        return@run keysMap
//    }
//
//    private val knownKeys: Set<String> = keysMap.keys
//
//    override fun createParser(): Parser<KeysExpression> {
//        return or(
//                specialKey(),
//                text
//        )
//    }
//
//    fun parse(keysExpression: String): KeysExpression {
//        TODO("not yet implemented")
//    }

}