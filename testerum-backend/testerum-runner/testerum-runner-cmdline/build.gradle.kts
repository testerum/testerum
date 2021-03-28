plugins {
    kotlin("jvm")
    id("com.testerum.build-dev-copy-runner-to-package-dir")
//    id("org.siouan.frontend-jdk8")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":report-generators"))
    implementation(project(":common-di"))
    implementation(project(":common-kotlin"))
    implementation(project(":common-cmdline"))
    implementation(project(":common-expression-evaluator"))
    implementation(project(":common-rdbms"))
    implementation(project(":testerum-runner-api"))
    implementation(project(":model"))
    implementation(project(":settings"))
    implementation(project(":testerum-scanner"))
    implementation(project(":file-service"))
    implementation(project(":project-manager"))
    implementation(project(":common-json"))
    implementation(project(":common-jdk"))
    implementation(project(":test-file-format"))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("org.slf4j:slf4j-simple")
    runtimeOnly("org.slf4j:jcl-over-slf4j")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.commons:commons-text")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
}

//frontend {
//    val nodeVersion: String by project
//
//    nodeDistributionProvided.set(false)
//    this@frontend.nodeVersion.set(nodeVersion)
//    assembleScript.set("run gradle-build")
//
//    yarnEnabled.set(false)
//
//    this@frontend.
//}
