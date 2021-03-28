plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-jdk"))
    implementation(project(":common-cmdline"))
    implementation(project(":common-kotlin"))
    implementation(project(":common-stats"))
    implementation(project(":common-httpclient"))
    implementation(project(":model"))
    implementation(project(":testerum-runner-api"))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("commons-io:commons-io")
}
