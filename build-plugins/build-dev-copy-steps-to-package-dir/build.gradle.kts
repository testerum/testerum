plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    gradlePluginPortal()
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

gradlePlugin {
    plugins {
        register("buildDevCopyStepsToPackageDir") {
            id = "com.testerum.build-dev-copy-steps-to-package-dir"
            implementationClass = "com.testerum.build_plugins.CopyStepsToPackageDirPlugin"
            displayName = "build-dev-copy-steps-to-package-dir"
            description = "Testerum plugin to copy steps & dependencies to the package dir"
        }
    }
}
