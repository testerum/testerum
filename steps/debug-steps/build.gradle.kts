plugins {
    kotlin("jvm")
    id("com.testerum.build-dev-copy-steps-to-package-dir")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(kotlin("stdlib-jdk8"))
    implementation("com.testerum:testerum-steps-api")
    implementation("org.apache.commons:commons-lang3")
}
