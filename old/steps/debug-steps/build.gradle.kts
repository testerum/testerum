plugins {
    kotlin("jvm")
    id("com.testerum.build-dev-copy-steps-to-package-dir")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.commons:commons-lang3")
}
