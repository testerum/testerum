package com.testerum_api.testerum_steps_api.transformer.impl

import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer

class NoTransformer: Transformer<Nothing> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = false

    override fun transform(toTransform: String, paramInfo: ParameterInfo)
            = throw RuntimeException("this transformer should never be called")

}
