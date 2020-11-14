package com.testerum.steps_maven_plugin

import com.testerum.common_fast_serialization.read_write.FastOutput
import com.testerum.common_kotlin.createDirectories
import com.testerum.scanner.step_lib_scanner.ExtensionsCacheLoader
import com.testerum.scanner.step_lib_scanner.ExtensionsScanner
import com.testerum.scanner.step_lib_scanner.model.ExtensionsScanConfig
import com.testerum.scanner.step_lib_scanner.model.cache_marshalling.ExtensionsScanResultMarshaller
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.BufferedOutputStream
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors

@Mojo(name = "run", defaultPhase = LifecyclePhase.PROCESS_CLASSES, threadSafe = true)
class TesterumStepsMojo : AbstractMojo() {

    @Parameter(defaultValue = "\${project}", required = true, readonly = true)
    private lateinit var project: MavenProject

    override fun execute() {
        val compileRootPath = Paths.get(project.build.outputDirectory)
        log.info("scanning $compileRootPath")

        val threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        try {
            val extensionsScanner = ExtensionsScanner(threadPool)

            val extensionsClassLoader = URLClassLoader(
                listOf(compileRootPath.toUri().toURL()).toTypedArray(),
                Thread.currentThread().contextClassLoader
            )
            val scanResult = extensionsScanner.scan(
                ExtensionsScanConfig(
                    overrideClassLoaders = extensionsClassLoader,
                    ignoreParentClassLoaders = false
                )
            )

            val destinationFile = Paths.get("${project.build.outputDirectory}/${ExtensionsCacheLoader.CACHE_PATH_IN_JAR}")
            destinationFile.parent?.createDirectories()

            Files.newOutputStream(destinationFile).use { outputStream ->
                BufferedOutputStream(outputStream).use { bufferedOutputStream ->
                    val output = FastOutput(bufferedOutputStream)

                    ExtensionsScanResultMarshaller.serialize("", scanResult, output)
                }
            }
        } finally {
            threadPool.shutdownNow()
            // no need for threadPool.awaitExecution() - assuming the scanner properly consumed all threads that it started
        }
    }

}
