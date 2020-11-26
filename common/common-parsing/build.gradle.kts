plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    api("org.jparsec:jparsec")
    implementation(kotlin("stdlib-jdk8"))
    implementation("commons-io:commons-io")
}
