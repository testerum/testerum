package com.testerum.api.annotations.steps

import com.testerum.api.transformer.Transformer
import com.testerum.api.transformer.impl.NoTransformer
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention
@MustBeDocumented
annotation class Param (
        val description: String = "",

        val transformer: KClass<out Transformer<Any?>> = NoTransformer::class,

        val required: Boolean = true

)
