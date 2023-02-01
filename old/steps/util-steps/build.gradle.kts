plugins {
    kotlin("jvm")
    id("com.testerum.build-dev-copy-steps-to-package-dir")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-expression-evaluator"))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))
}
