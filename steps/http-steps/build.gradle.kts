plugins {
    kotlin("jvm")
    id("com.testerum.build-dev-copy-steps-to-package-dir")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-di"))
    implementation(project(":common-json"))
    implementation(project(":common-json-diff"))
    implementation(project(":common-httpclient"))
    implementation(project(":common-di"))
    implementation(project(":assertion-functions"))
    implementation(project(":model"))
    implementation(project(":step-transformer-utils"))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.tomakehurst:wiremock-standalone")
    implementation("org.apache.commons:commons-lang3:3.8.1")
}
