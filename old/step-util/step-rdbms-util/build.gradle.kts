plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-jdk"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("com.google.guava:guava")
    implementation("commons-io:commons-io")
    implementation("org.flywaydb:flyway-core")
}
