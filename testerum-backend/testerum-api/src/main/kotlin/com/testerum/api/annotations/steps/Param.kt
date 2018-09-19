package com.testerum.api.annotations.steps

import com.testerum.api.annotations.util.AnnotationConstants.ANNOTATION_NULL
import com.testerum.api.transformer.Transformer
import com.testerum.api.transformer.impl.NoTransformer
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention
@MustBeDocumented
annotation class Param (
        val description: String = ANNOTATION_NULL,

        val transformer: KClass<out Transformer<Any?>> = NoTransformer::class,

        val required: Boolean = true

)
