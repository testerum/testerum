package com.testerum.runner_cmdline.transformer.builtin.math

import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import java.math.BigInteger

object BigIntegerTransformer : Transformer<BigInteger> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == BigInteger::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): BigInteger
            = try {
                BigInteger(toTransform)
            } catch (e: Exception) {
                throw IllegalArgumentException("[$toTransform] is not a valid BigInteger")
            }

}