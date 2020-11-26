plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":model"))
    implementation(project(":testerum-scanner"))
    implementation(project(":testerum-scanner-it-steplib1"))
    implementation(project(":testerum-scanner-it-steplib2"))
    implementation(project(":testerum-scanner-it-steplib3-java"))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
}
