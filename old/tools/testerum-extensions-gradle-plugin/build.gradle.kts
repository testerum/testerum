plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(platform(project(":build-platform")))
}

gradlePlugin {
    plugins {
        create("testerumExtensionsGradlePlugin") {
            id = "com.testerum.testerum-extensions-gradle-plugin"
            implementationClass = "com.testerum.extensions_gradle_plugin.ExtensionsGradlePlugin"
        }
    }
}
