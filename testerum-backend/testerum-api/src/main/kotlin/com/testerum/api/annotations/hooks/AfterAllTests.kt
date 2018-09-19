package com.testerum.api.annotations.hooks

import com.testerum.api.annotations.util.AnnotationConstants.ANNOTATION_NULL

@Target(AnnotationTarget.FUNCTION)
@Retention
@MustBeDocumented
annotation class AfterAllTests(

        val description: String = ANNOTATION_NULL,

        /** order of hook execution; hooks with higher order are run after hooks with lower order */
        val order: Int = HooksConstants.DEFAULT_ORDER

)
