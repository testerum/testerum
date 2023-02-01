plugins {
    kotlin("jvm")
    id("com.testerum.build-dev-copy-steps-to-package-dir")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-jdk"))
    implementation(project(":common-kotlin"))
    implementation(project(":common-parsing"))
    implementation(project(":common-di"))
    implementation(project(":common-expression-evaluator"))
    implementation(project(":settings"))
    implementation(project(":model"))
    implementation(project(":file-service"))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("org.seleniumhq.selenium:selenium-java")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.slf4j:slf4j-simple")
}
