package com.testerum_api.testerum_steps_api.annotations.steps

import com.testerum_api.testerum_steps_api.annotations.util.AnnotationConstants.ANNOTATION_NULL

@Target(AnnotationTarget.FUNCTION)
@Retention
@MustBeDocumented
annotation class Given(

        /** the step's pattern */
        val value: String,

        val description: String = ANNOTATION_NULL,

        val tags: Array<String> = []

)
