package com.testerum.runner.transformer.builtin.atomic

import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.runner.transformer.builtin.lang.IntegerTransformer
import java.util.concurrent.atomic.AtomicInteger

class AtomicIntegerTransformer(private val integerTransformer: IntegerTransformer) : Transformer<AtomicInteger> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == AtomicInteger::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): AtomicInteger
            = AtomicInteger(
                integerTransformer.transform(toTransform, paramInfo)
            )

}
