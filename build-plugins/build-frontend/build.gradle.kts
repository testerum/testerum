plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.siouan:frontend-gradle-plugin-jdk8:5.0.1")
}

gradlePlugin {
    plugins {
        register("buildFrontend") {
            id = "com.testerum.build-frontend"
            implementationClass = "com.testerum.build_plugins.build_frontend.BuildFrontendPlugin"
            displayName = "build-frontend"
            description = "Testerum plugin to build a NodeJS/NPM project"
        }
    }
}
