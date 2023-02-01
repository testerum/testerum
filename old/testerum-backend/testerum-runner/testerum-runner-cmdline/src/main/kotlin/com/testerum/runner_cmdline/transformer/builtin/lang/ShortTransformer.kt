package com.testerum.runner_cmdline.transformer.builtin.lang

import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer

object ShortTransformer : Transformer<Short> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == java.lang.Short::class.java) || (paramInfo.type == java.lang.Short.TYPE)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): Short
            = try {
                java.lang.Short.parseShort(toTransform)
            } catch (e: Exception) {
                throw IllegalArgumentException("[$toTransform] is not a valid Short")
            }

}
