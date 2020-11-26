plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-jdk"))
    implementation(project(":common-kotlin"))
    implementation(project(":common-nashorn"))

    implementation("com.testerum:testerum-steps-api")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("org.apache.commons:commons-lang3")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jdom:jdom2")
    implementation("jaxen:jaxen")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation(project(":common-expression-evaluator"))
    testImplementation("org.assertj:assertj-core")
}
