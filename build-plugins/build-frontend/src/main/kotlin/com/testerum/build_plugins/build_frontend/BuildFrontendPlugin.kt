package com.testerum.build_plugins.build_frontend

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.PathSensitivity
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
        val nodeJsDestinationDirectory = "${rootProject.projectDir}/build/node"

        // apply and configure the frontend plugin
        pluginManager.apply("org.siouan.frontend-jdk8")
        frontend {
            nodeDistributionProvided.set(false)
            nodeVersion.set(NODE_VERSION)
            nodeInstallDirectory.set(file(nodeJsDestinationDirectory))

            yarnEnabled.set(false)

            assembleScript.set("run gradle-build")
        }

        // run the frontend tasks only when building for production
        tasks.findAll(closureOf<Task> { name.endsWith("Frontend") })
            .forEach { task ->
                task.onlyIf {
                    !project.hasProperty("skipFrontend")
                }
            }

        tasks.named("installNode")
            .configure {
                inputs.property("nodeVersion", NODE_VERSION)

                outputs.dir(nodeJsDestinationDirectory)
                    .withPropertyName("nodeJsDestinationDir")

                outputs.cacheIf { true }
            }

        tasks.named("installFrontend")
            .configure {
                inputs.file("package.json")
                    .withPropertyName("packageJsonFile")
                    .withPathSensitivity(PathSensitivity.RELATIVE)

                outputs.dir(layout.projectDirectory.dir("node_modules"))
                    .withPropertyName("nodeModulesDir")

                outputs.cacheIf { true }
            }

        tasks.named("assembleFrontend")
            .configure {
                inputs
                    .files(
                        fileTree("src"),
                        fileTree("e2e")
                    )
                    .withPropertyName("sourceDirs")
                    .withPathSensitivity(PathSensitivity.RELATIVE)
                inputs
                    .files(
                        ".browserslistrc",
                        "angular.json",
                        "gradle-build.js",
                        "karma.conf.js",
                        "package.json",
                        "tsconfig.app.json",
                        "tsconfig.json",
                        "tsconfig.spec.json",
                        "tslint.json"
                    )
                    .withPropertyName("configFiles")
                    .withPathSensitivity(PathSensitivity.RELATIVE)

                outputs.dir("dist")
                    .withPropertyName("distDir")

                outputs.cacheIf { true }
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
        extensions.configure("frontend", configure)

}
