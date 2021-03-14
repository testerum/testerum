plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-kotlin"))
    api(project(":common-parsing"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("org.jparsec:jparsec")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
}
