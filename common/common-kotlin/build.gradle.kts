plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-jdk"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("commons-io:commons-io")
}
