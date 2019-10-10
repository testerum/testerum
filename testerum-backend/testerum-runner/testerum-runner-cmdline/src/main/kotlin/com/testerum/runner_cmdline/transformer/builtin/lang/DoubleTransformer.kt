package com.testerum.runner_cmdline.transformer.builtin.lang

import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer

object DoubleTransformer : Transformer<Double> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == java.lang.Double::class.java) || (paramInfo.type == java.lang.Double.TYPE)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): Double
            = try {
                java.lang.Double.parseDouble(toTransform)
            } catch (e: Exception) {
                throw IllegalArgumentException("[$toTransform] is not a valid Double")
            }

}
