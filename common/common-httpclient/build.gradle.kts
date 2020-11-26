plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":model"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    api("org.apache.httpcomponents:httpclient")
}
