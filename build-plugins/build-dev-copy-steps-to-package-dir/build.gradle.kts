plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

gradlePlugin {
    plugins {
        register("buildDevCopyStepsToPackageDir") {
            id = "com.testerum.build-dev-copy-steps-to-package-dir"
            implementationClass = "com.testerum.build_plugins.copy_steps.CopyStepsToPackageDirPlugin"
            displayName = "build-dev-copy-steps-to-package-dir"
            description = "Testerum plugin to copy steps & dependencies to the package dir"
        }
    }
}
