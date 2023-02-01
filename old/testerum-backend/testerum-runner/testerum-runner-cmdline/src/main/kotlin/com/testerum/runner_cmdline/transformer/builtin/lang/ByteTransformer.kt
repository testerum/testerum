package com.testerum.runner_cmdline.transformer.builtin.lang

import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer

object ByteTransformer : Transformer<Byte> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == java.lang.Byte::class.java) || (paramInfo.type == java.lang.Byte.TYPE)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): Byte
            = try {
                java.lang.Byte.parseByte(toTransform)
            } catch (e: Exception) {
                throw IllegalArgumentException("[$toTransform] is not a valid Byte")
            }

}
