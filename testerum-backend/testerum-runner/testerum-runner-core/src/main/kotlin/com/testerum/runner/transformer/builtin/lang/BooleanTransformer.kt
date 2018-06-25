package com.testerum.runner.transformer.builtin.lang

import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer

object BooleanTransformer : Transformer<Boolean> {

    private val TRUE_VALUES  = setOf("true" , "yes")
    private val FALSE_VALUES = setOf("false", "no")

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == java.lang.Boolean::class.java) || (paramInfo.type == java.lang.Boolean.TYPE)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): Boolean {
        if (TRUE_VALUES.contains(toTransform)) {
            return true
        }
        if (FALSE_VALUES.contains(toTransform)) {
            return false
        }

        throw IllegalArgumentException("[$toTransform] is not a valid boolean; valid values are: ${TRUE_VALUES}, ${FALSE_VALUES}")
    }

}