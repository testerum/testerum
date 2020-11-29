plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-di"))
    implementation(project(":model"))
    implementation(project(":settings"))
    implementation(project(":file-service"))
    implementation(project(":testerum-scanner"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("com.google.guava:guava")
}
