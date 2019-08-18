package com.testerum_api.testerum_steps_api.annotations.settings

import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingType

@Suppress("DEPRECATED_JAVA_ANNOTATION")
@Target(AnnotationTarget.CLASS)
@Retention
@MustBeDocumented
@java.lang.annotation.Repeatable(DeclareSettings::class)
annotation class DeclareSetting(

        val key: String,

        val label: String,

        val type: SettingType = SettingType.TEXT,

        val defaultValue: String = "",

        val enumValues: Array<String> = [],

        val description: String = "",

        val category: String = ""

)
