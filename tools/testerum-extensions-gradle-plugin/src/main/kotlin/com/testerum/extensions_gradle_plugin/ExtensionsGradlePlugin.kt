package com.testerum.extensions_gradle_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class ExtensionsGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.task("hello") {
            doLast {
                println("Hello from ${this.javaClass.simpleName}")
            }
        }
    }
}
