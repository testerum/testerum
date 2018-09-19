package com.testerum.api.annotations.steps

import com.testerum.api.annotations.util.AnnotationConstants.ANNOTATION_NULL

@Target(AnnotationTarget.FUNCTION)
@Retention
@MustBeDocumented
annotation class Then(

        /** the step's pattern */
        val value: String,

        val description: String = ANNOTATION_NULL,

        val tags: Array<String> = []

)
