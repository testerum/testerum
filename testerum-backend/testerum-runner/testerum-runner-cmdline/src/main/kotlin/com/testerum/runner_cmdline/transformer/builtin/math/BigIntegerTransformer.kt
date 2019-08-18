package com.testerum.runner_cmdline.transformer.builtin.math

import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer
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
