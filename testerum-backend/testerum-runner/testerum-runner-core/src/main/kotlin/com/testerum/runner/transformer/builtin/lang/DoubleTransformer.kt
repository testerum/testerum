package com.testerum.runner.transformer.builtin.lang

import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer

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