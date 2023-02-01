package com.testerum.runner_cmdline.transformer.builtin.lang

import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer

object IntegerTransformer : Transformer<Int> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == java.lang.Integer::class.java) || (paramInfo.type == java.lang.Integer.TYPE)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): Int
            = try {
                java.lang.Integer.parseInt(toTransform)
            } catch (e: Exception) {
                throw IllegalArgumentException("[$toTransform] is not a valid Integer")
            }

}
