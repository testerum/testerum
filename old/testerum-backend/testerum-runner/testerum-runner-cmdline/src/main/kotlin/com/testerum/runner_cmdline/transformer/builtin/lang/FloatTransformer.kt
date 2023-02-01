package com.testerum.runner_cmdline.transformer.builtin.lang

import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer

object FloatTransformer : Transformer<Float> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == java.lang.Float::class.java) || (paramInfo.type == java.lang.Float.TYPE)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): Float
            = try {
                java.lang.Float.parseFloat(toTransform)
            } catch (e: Exception) {
                throw IllegalArgumentException("[$toTransform] is not a valid Float")
            }

}
