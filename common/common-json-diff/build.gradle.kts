plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-di"))
    implementation(project(":common-json-diff-util"))
    implementation(project(":assertion-functions"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
    testImplementation("commons-io:commons-io")
    testImplementation("org.slf4j:jcl-over-slf4j")
    testImplementation("org.slf4j:slf4j-simple")
}
