package com.testerum.runner.transformer.builtin.lang

import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer

object LongTransformer : Transformer<Long> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == java.lang.Long::class.java) || (paramInfo.type == java.lang.Long.TYPE)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): Long
            = try {
                java.lang.Long.parseLong(toTransform)
            } catch (e: Exception) {
                throw IllegalArgumentException("[$toTransform] is not a valid Long")
            }

}