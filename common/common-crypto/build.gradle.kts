plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(kotlin("stdlib-jdk8"))

    implementation("org.bouncycastle:bcpkix-jdk15on:1.60")
    implementation("org.apache.commons:commons-text:1.6")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.apache.commons:commons-lang3")
}

