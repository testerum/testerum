import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

val javaVersion = "17"
java.sourceCompatibility = JavaVersion.toVersion(javaVersion)
java.targetCompatibility = java.sourceCompatibility

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = javaVersion
    }
}

application {
    mainClass.set("testerum.AppKt")
}
