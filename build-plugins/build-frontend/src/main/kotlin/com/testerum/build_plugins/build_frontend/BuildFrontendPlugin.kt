package com.testerum.build_plugins.build_frontend

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionAware
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.closureOf
import org.siouan.frontendgradleplugin.infrastructure.gradle.FrontendExtension

class BuildFrontendPlugin : Plugin<Project> {

    companion object {
        private const val NODE_VERSION = "14.17.4"
    }

    override fun apply(target: Project) {
        target.doApply()
    }

    private fun Project.doApply() {
        // apply and configure the frontend plugin
        pluginManager.apply("org.siouan.frontend-jdk8")
        frontend {
            nodeDistributionProvided.set(false)
            nodeVersion.set(NODE_VERSION)
            nodeInstallDirectory.set(file("${rootProject.projectDir}/build/node"))

            yarnEnabled.set(false)

            assembleScript.set("run gradle-build")
        }

        // run the frontend tasks only when building for production
        tasks.findAll(closureOf<Task> { name.endsWith("Frontend") })
            .forEach { task ->
                task.onlyIf {
                    project.hasProperty("production")
                }
            }


        val packageFrontendToJar = tasks.register("packageFrontendToJar", Jar::class.java) {
            onlyIf {
                project.hasProperty("production")
            }

            dependsOn(tasks.named("assembleFrontend"))

            from("${project.projectDir}/dist")
            into("frontend")
            archiveBaseName.set(project.name)
            archiveClassifier.set("dist")
        }

        tasks.named("assemble") {
            dependsOn(packageFrontendToJar)
        }

        val frontendJars = configurations.create("frontendJars") {
            isCanBeConsumed = true
            isCanBeResolved = false
        }

        artifacts {
            add(frontendJars.name, packageFrontendToJar)
        }
    }

    private fun Project.frontend(configure: Action<FrontendExtension>): Unit =
        (this as ExtensionAware).extensions.configure("frontend", configure)


}
