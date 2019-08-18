package com.testerum_api.testerum_steps_api.annotations.hooks

import com.testerum_api.testerum_steps_api.annotations.util.AnnotationConstants.ANNOTATION_NULL

@Target(AnnotationTarget.FUNCTION)
@Retention
@MustBeDocumented
annotation class BeforeAllTests(

        val description: String = ANNOTATION_NULL,

        /** order of hook execution; hooks with higher order are run after hooks with lower order */
        val order: Int = com.testerum_api.testerum_steps_api.annotations.hooks.HooksConstants.DEFAULT_ORDER

)
