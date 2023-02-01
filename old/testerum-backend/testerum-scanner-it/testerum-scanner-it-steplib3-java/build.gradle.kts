plugins {
    java
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation("com.testerum:testerum-steps-api")
}
