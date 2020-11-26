plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-kotlin"))

    implementation(kotlin("stdlib-jdk8"))
}
