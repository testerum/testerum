plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(kotlin("stdlib-jdk8"))
    api("org.jparsec:jparsec")
    implementation("commons-io:commons-io")
}
