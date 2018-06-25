package com.testerum.runner.transformer.builtin.lang

import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer

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