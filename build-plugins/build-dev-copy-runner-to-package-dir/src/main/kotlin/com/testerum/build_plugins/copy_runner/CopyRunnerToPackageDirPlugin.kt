package com.testerum.build_plugins.copy_runner

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class CopyRunnerToPackageDirPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        // the withType is required to let Gradle know we need to apply the base plugin before ours
        // this is because we need to make "assemble" depend on our task
        target.plugins.withType<BasePlugin> {
            target.configureCopyTask()
        }
    }

    private fun Project.configureCopyTask() {
        val copyRunnerToPackageDirTask = tasks.register("copyRunnerToPackageDir", Copy::class) {
            onlyIf {
                !project.hasProperty("production")
            }

            from(configurations.named("runtimeClasspath"))
            from(tasks.named("jar"))
            into("${rootProject.projectDir}/package/runner/lib")
        }

        tasks.named("assemble").configure {
            dependsOn(copyRunnerToPackageDirTask)
        }
    }
}
