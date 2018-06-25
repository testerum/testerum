package com.testerum.settings

import com.testerum.api.test_context.settings.model.SettingType
import com.testerum.api.test_context.settings.model.Setting

object SystemSettings {
    val TESTERUM_INSTALL_DIRECTORY = Setting(
            key = "testerum.packageDirectory",
            type = SettingType.TEXT,
            defaultValue = System.getProperty("testerum.packageDirectory"),
            description = "Directory where the Testerum application is installed",
            category = "Application")

    val BUILT_IN_BASIC_STEPS_DIRECTORY = Setting(
            key = "testerum.builtInBasicStepsDirectory",
            type = SettingType.TEXT,
            defaultValue = "{{testerum.packageDirectory}}/basic_steps",
            description = "Directory where the build in Basic Steps jar files and dependencies are located",
            category = "Application")

    val REPOSITORY_DIRECTORY = Setting(
            key = "testerum.repositoryDirectory",
            type = SettingType.FILESYSTEM_DIRECTORY,
            defaultValue = null,
            description = "Directory path where all your work will be saved",
            category = "Application")

    val USER_BASIC_STEPS_DIRECTORY = Setting(
            key = "testerum.userBasicStepsDirectory",
            type = SettingType.FILESYSTEM_DIRECTORY,
            defaultValue = null,
            description = "Directory where the user defined Basic Steps jar files and dependencies are located",
            category = "Application")

    val JDBC_DRIVERS_DIRECTORY = Setting(
            key = "testerum.jdbcDriversDirectory",
            type = SettingType.FILESYSTEM_DIRECTORY,
            defaultValue = "{{testerum.packageDirectory}}/relational_database_drivers",
            description = "Directory where the JDBC Drivers jar files and their descriptors are located",
            category = "Relational Database")

    val allSystemSettings: List<Setting> = mutableListOf(
            TESTERUM_INSTALL_DIRECTORY,
            REPOSITORY_DIRECTORY,
            BUILT_IN_BASIC_STEPS_DIRECTORY,
            USER_BASIC_STEPS_DIRECTORY,
            JDBC_DRIVERS_DIRECTORY
    )
}