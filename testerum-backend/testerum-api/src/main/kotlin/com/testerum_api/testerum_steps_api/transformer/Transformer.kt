package com.testerum_api.testerum_steps_api.transformer

interface Transformer<out T> {

    fun canTransform(paramInfo: ParameterInfo): Boolean

    fun transform(toTransform: String, paramInfo: ParameterInfo): T

}
