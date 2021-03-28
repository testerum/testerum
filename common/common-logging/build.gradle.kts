plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")

    runtimeOnly("org.slf4j:jcl-over-slf4j")
    runtimeOnly("org.slf4j:log4j-over-slf4j")
    runtimeOnly("ch.qos.logback:logback-classic")
}
