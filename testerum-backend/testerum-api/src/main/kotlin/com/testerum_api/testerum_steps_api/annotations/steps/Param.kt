package com.testerum_api.testerum_steps_api.annotations.steps

import com.testerum_api.testerum_steps_api.annotations.util.AnnotationConstants.ANNOTATION_NULL
import com.testerum_api.testerum_steps_api.transformer.Transformer
import com.testerum_api.testerum_steps_api.transformer.impl.NoTransformer
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention
@MustBeDocumented
annotation class Param (
        val description: String = ANNOTATION_NULL,

        val transformer: KClass<out Transformer<Any?>> = NoTransformer::class,

        val required: Boolean = true

)
