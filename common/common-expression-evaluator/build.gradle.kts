plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-nashorn"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("org.apache.commons:commons-text")
    implementation("org.javadelight:delight-nashorn-sandbox")
    implementation("com.github.javafaker:javafaker")
    implementation("com.github.mifmif:generex")
}
