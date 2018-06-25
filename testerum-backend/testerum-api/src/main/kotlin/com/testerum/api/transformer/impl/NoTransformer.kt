package com.testerum.api.transformer.impl

import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer

class NoTransformer: Transformer<Nothing> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = false

    override fun transform(toTransform: String, paramInfo: ParameterInfo)
            = throw RuntimeException("this transformer should never be called")

}
