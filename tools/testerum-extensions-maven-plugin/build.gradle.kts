plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-kotlin"))
    implementation(project(":testerum-scanner"))
    implementation(project(":common-fast-serialization"))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))
    implementation("io.github.classgraph:classgraph")
    implementation("org.apache.maven:maven-core")
    implementation("org.apache.maven:maven-plugin-api")
    implementation("org.apache.maven.plugin-tools:maven-plugin-annotations")
}
