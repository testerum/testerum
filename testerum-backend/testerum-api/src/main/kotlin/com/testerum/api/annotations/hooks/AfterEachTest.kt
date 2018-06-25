package com.testerum.api.annotations.hooks

@Target(AnnotationTarget.FUNCTION)
@Retention
@MustBeDocumented
annotation class AfterEachTest(

        val description: String = "",

        /** order of hook execution; hooks with higher order are run after hooks with lower order */
        val order: Int = HooksConstants.DEFAULT_ORDER

)