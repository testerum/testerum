plugins {
    kotlin("jvm")
    id("com.testerum.build-dev-copy-steps-to-package-dir")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-json"))
    implementation(project(":common-di"))
    implementation(project(":common-json-diff"))
    implementation(project(":assertion-functions"))
    implementation(project(":settings"))
    implementation(project(":common-rdbms"))
    implementation(project(":step-rdbms-util"))
    implementation(project(":step-transformer-utils"))
    implementation(project(":model"))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava")
}
