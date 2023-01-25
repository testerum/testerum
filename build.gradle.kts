
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") apply false
    java
}

val production: String? by rootProject

allprojects {
    group = "com.testerum"
    version = "develop-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.allWarningsAsErrors = true
        kotlinOptions.apiVersion = "1.8"
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf(
            "-progressive"
        )
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    if (production != null) {
        setProperty("production", production)
    }
}
