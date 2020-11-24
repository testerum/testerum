plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

val frontendClasspath: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}
configurations.implementation.configure {
    extendsFrom(frontendClasspath)
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-angular"))
    implementation(project(":common-kotlin"))
    implementation(project(":report-generators"))
    implementation(project(":testerum-runner-api"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    runtimeOnly("org.slf4j:jcl-over-slf4j")
    runtimeOnly("org.slf4j:log4j-over-slf4j")
    runtimeOnly("ch.qos.logback:logback-core")
    runtimeOnly("ch.qos.logback:logback-classic")
    implementation("org.logback-extensions:logback-ext-spring")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    implementation("org.springframework:spring-core:5.2.8.RELEASE")


    frontendClasspath(project(mapOf(
        "path" to ":report-server-frontend",
        "configuration" to "frontendJars"
    )))
}

