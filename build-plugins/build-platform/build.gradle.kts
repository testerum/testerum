plugins {
    `java-platform`
    `maven-publish`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        // internal dependencies
        for (subproject in rootProject.subprojects) {
            if (subproject.name == name) {
                continue
            }

            api(project(subproject.path))
        }

        // testerum-api
        api("com.testerum:testerum-steps-api:develop-SNAPSHOT")

        // kotlin
        val kotlinVersion: String by project
        api(kotlin("stdlib-jdk8", kotlinVersion))
        api(kotlin("reflect", kotlinVersion))

        // logging
        val slf4jVersion = "1.7.25"
        val logbackVersion = "1.2.2"
        api("org.slf4j:slf4j-api:$slf4jVersion")
        api("org.slf4j:slf4j-simple:$slf4jVersion")
        api("org.slf4j:log4j-over-slf4j:$slf4jVersion")
        api("org.slf4j:jcl-over-slf4j:$slf4jVersion")
        api("ch.qos.logback:logback-core:$logbackVersion")
        api("ch.qos.logback:logback-classic:$logbackVersion")

        // nashorn
        api("org.javadelight:delight-nashorn-sandbox:0.1.19")

        // jackson
        val jacksonVersion = "2.11.3"
        api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
        api("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
        api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
        api("com.fasterxml.jackson.module:jackson-module-afterburner:$jacksonVersion")
        api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
        api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
        api("com.fasterxml.jackson.datatype:jackson-datatype-guava:$jacksonVersion")
        api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
        api("org.logback-extensions:logback-ext-spring:0.1.1")

        // xml
        api("org.jdom:jdom2:2.0.6")
        api("jaxen:jaxen:1.2.0")

        // jparsec - file format parsing library
        api("org.jparsec:jparsec:3.0")

        // janino: compile java classes at runtime
        api("org.codehaus.janino:janino:3.0.7")

        // classgraph - annotations scanning library
        api("io.github.classgraph:classgraph:4.8.90")

        // http client
        api("org.apache.httpcomponents:httpclient:4.5.6")

        // http server
        api("javax.servlet:javax.servlet-api:4.0.1")

        // wiremock
        api("com.github.tomakehurst:wiremock-standalone:2.18.0")

        // database
        api("org.flywaydb:flyway-core:4.1.2")

        // process handling
        api("org.zeroturnaround:zt-exec:1.10")
        api("org.zeroturnaround:zt-process-killer:1.8")

        // maven
        api("org.apache.maven:maven-core:3.0.5")
        api("org.apache.maven:maven-plugin-api:3.0.5")
        api("org.apache.maven.plugin-tools:maven-plugin-annotations:3.4")

        // selenium
        api("org.seleniumhq.selenium:selenium-java:3.13.0")

        // quartz
        api("org.quartz-scheduler:quartz:2.3.1")

        // misc libraries
        api("org.apache.commons:commons-lang3:3.8.1")
        api("org.apache.commons:commons-text:1.6")
        api("commons-io:commons-io:2.6")
        api("com.google.guava:guava:22.0")
        api("com.github.javafaker:javafaker:0.16")
        api("com.github.mifmif:generex:1.0.2")

        // test libraries
        val junitJupiterVersion = "5.6.2"
        api("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
        api("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
        api("org.assertj:assertj-core:3.18.1")
        api("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    }
}

publishing {
    publications {
        create<MavenPublication>("o2pPlatform") {
            from(components["javaPlatform"])
        }
    }
}
