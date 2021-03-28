rootProject.name = "testerum"

pluginManagement {
    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        id("org.siouan.frontend-jdk8") version "4.0.1"
        id("org.springframework.boot") version "2.3.3.RELEASE"
    }

    repositories {
        gradlePluginPortal()
    }
}

includeBuild("build-plugins/build-dev-copy-steps-to-package-dir")
includeBuild("build-plugins/build-dev-copy-runner-to-package-dir")

includeWithDir("build-platform", "build-plugins")

includeWithDir("common-jdk", "common")
includeWithDir("common-kotlin", "common")
includeWithDir("common-logging", "common")
includeWithDir("common-nashorn", "common")
includeWithDir("common-expression-evaluator", "common")
includeWithDir("common-crypto", "common")
includeWithDir("common-di", "common")
includeWithDir("common-parsing", "common")
includeWithDir("common-json-diff-util", "common")
includeWithDir("assertion-functions", "common")
includeWithDir("common-angular", "common")
includeWithDir("common-cmdline", "common")
includeWithDir("common-fsnotifier", "common")
includeWithDir("common-httpclient", "common")
includeWithDir("common-rdbms", "common")
includeWithDir("common-profiles", "common")
includeWithDir("common-json", "common")
includeWithDir("common-fast-serialization", "common")
includeWithDir("common-stats", "common")
includeWithDir("common-encrypted-prefs", "common")
includeWithDir("common-json-diff", "common")
include("model")
include("cloud-client")
include("test-file-format")
include("settings")
includeWithDir("testerum-scanner", "testerum-backend")
includeWithDir("testerum-runner-api", "testerum-backend/testerum-runner")
include("file-service")
include("project-manager")
includeWithDir("report-generators", "report")
includeWithDir("report-server-frontend", "report/report-server")
includeWithDir("report-server-backend", "report/report-server")
includeWithDir("step-rdbms-util", "step-util")
includeWithDir("step-transformer-utils", "step-util")
includeWithDir("testerum-extensions-maven-plugin", "tools")
includeWithDir("testerum-extensions-gradle-plugin", "tools")
includeWithDir("assertion-steps", "steps")
includeWithDir("debug-steps", "steps")
includeWithDir("json-steps", "steps")
includeWithDir("rdbms-steps", "steps")
includeWithDir("http-steps", "steps")
includeWithDir("selenium-steps", "steps")
includeWithDir("test-steps", "steps")
includeWithDir("util-steps", "steps")
includeWithDir("vars-steps", "steps")
includeWithDir("testerum-scanner-it-steplib1", "testerum-backend/testerum-scanner-it")
includeWithDir("testerum-scanner-it-steplib2", "testerum-backend/testerum-scanner-it")
includeWithDir("testerum-scanner-it-steplib3-java", "testerum-backend/testerum-scanner-it")
includeWithDir("testerum-scanner-it-test", "testerum-backend/testerum-scanner-it")
includeWithDir("testerum-runner-cmdline", "testerum-backend/testerum-runner")

fun includeWithDir(
    projectName: String,
    dirPrefix: String
) {
    include(projectName)
    project(":$projectName").projectDir = file("$dirPrefix/$projectName")
}
