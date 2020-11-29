plugins {
    id("org.siouan.frontend-jdk8")
}

frontend {
    val nodeVersion: String by project

    nodeDistributionProvided.set(false)
    this@frontend.nodeVersion.set(nodeVersion)
    assembleScript.set("run maven-build")

    yarnEnabled.set(false)
}

tasks.findAll(closureOf<Task> { name.endsWith("Frontend") })
    .forEach { task ->
        task.onlyIf {
            project.hasProperty("production")
        }
    }

val packageFrontendToJar by tasks.registering(Jar::class) {
    onlyIf {
        project.hasProperty("production")
    }

    dependsOn(tasks.named("assembleFrontend"))

    from("${project.projectDir}/dist")
    into("frontend")
    archiveBaseName.set(project.name)
    archiveClassifier.set("dist")
}

tasks.assemble.configure {
    dependsOn(packageFrontendToJar)
}

val frontendJars by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(frontendJars.name, packageFrontendToJar)
}
