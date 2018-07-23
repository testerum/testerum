package com.testerum.api.annotations.steps

@Target(AnnotationTarget.FUNCTION)
@Retention
@MustBeDocumented
annotation class Given(

        /** the step's pattern */
        val value: String,

        val description: String = "",

        val tags: Array<String> = []

)