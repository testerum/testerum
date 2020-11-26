plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-jdk"))
    implementation(project(":common-kotlin"))
    implementation(project(":common-json"))
    implementation(project(":common-encrypted-prefs"))
    implementation(project(":common-parsing"))
    implementation(project(":common-di"))
    implementation(project(":test-file-format"))
    implementation(project(":model"))
    implementation(project(":testerum-scanner"))
    implementation(project(":testerum-runner-api"))
    implementation(project(":settings"))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
}
