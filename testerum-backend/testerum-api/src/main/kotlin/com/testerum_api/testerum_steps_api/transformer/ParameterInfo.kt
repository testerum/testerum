package com.testerum_api.testerum_steps_api.transformer

import java.lang.reflect.Type

data class ParameterInfo(val type: Class<*>,
                         val parametrizedType: Type,
                         val transformerClass: Class<out Transformer<Any?>>?,
                         val required: Boolean)
