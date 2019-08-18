package com.testerum_api.testerum_steps_api.annotations.settings

@Target(AnnotationTarget.CLASS)
@Retention
@MustBeDocumented
annotation class DeclareSettings(
        // note:
        //   This class is only required because currently Kotlin doesn't support repeated annotations (see https://youtrack.jetbrains.com/issue/KT-12794 ).
        //   In Java, we can just directly repeat the @DeclareSetting annotation (no need for this container class)

        val value: Array<DeclareSetting>
)
