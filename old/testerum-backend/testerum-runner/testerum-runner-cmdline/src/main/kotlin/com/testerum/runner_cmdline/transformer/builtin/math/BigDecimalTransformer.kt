package com.testerum.runner_cmdline.transformer.builtin.math

import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer
import java.math.BigDecimal

object BigDecimalTransformer : Transformer<BigDecimal> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == BigDecimal::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): BigDecimal
            = try {
                BigDecimal(toTransform)
            } catch (e: Exception) {
                throw IllegalArgumentException("[$toTransform] is not a valid BigDecimal")
            }

}
