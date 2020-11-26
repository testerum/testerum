plugins {
    kotlin("jvm")
}

dependencies {
    implementation(platform(project(":build-platform")))

    implementation(project(":common-jdk"))
    implementation(project(":common-kotlin"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-api")
    implementation("org.zeroturnaround:zt-exec")
    implementation("org.zeroturnaround:zt-process-killer")
}


// -------------------- prod: package native binaries --------------------
val packageNativeBinaries by tasks.registering(Tar::class) {
    onlyIf {
        project.hasProperty("production")
    }

    from("${project.projectDir}/src/main/assembly/package")
    archiveBaseName.set(project.name)
    archiveClassifier.set("native-binaries")
    archiveExtension.set("tar.gz")
    compression = Compression.GZIP
}

tasks.assemble.configure {
    dependsOn(packageNativeBinaries)
}

// -------------------- dev: copy native binaries to package dir --------------------
val copyNativeBinariesToPackageDir by tasks.registering(Copy::class) {
    onlyIf {
        !project.hasProperty("production")
    }

    from("${project.projectDir}/src/main/assembly/package")
    into("${rootProject.projectDir}/package/fsnotifier")
}

tasks.assemble.configure {
    dependsOn(copyNativeBinariesToPackageDir)
}
