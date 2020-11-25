/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("com.testerum.java-conventions")
}

dependencies {
    implementation(project(":testerum-runner-cmdline"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    implementation("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    testImplementation(project(":assertion-steps"))
    testImplementation(project(":debug-steps"))
    testImplementation(project(":http-steps"))
    testImplementation(project(":json-steps"))
    testImplementation(project(":rdbms-steps"))
    testImplementation(project(":selenium-steps"))
    testImplementation(project(":util-steps"))
    testImplementation(project(":vars-steps"))
}

description = "testerum-junit"