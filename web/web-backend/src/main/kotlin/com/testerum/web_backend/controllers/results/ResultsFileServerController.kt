package com.testerum.web_backend.controllers.results

import com.testerum.web_backend.services.dirs.FrontendDirs
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler
import org.springframework.web.servlet.resource.ResourceResolver
import org.springframework.web.servlet.resource.ResourceResolverChain
import java.util.*
import javax.servlet.http.HttpServletRequest
import java.nio.file.Path as JavaPath

@Qualifier("resultsFileServerController")
class ResultsFileServerController(private val frontendDirs: FrontendDirs) : ResourceHttpRequestHandler()  {

    // the URL path at which this controller is available is configured in "spring_web.xml" (look for the SimpleUrlHandlerMapping bean)

    init {
        resourceResolvers = listOf(
                object: ResourceResolver {
                    override fun resolveUrlPath(resourcePath: String, locations: MutableList<out Resource>, chain: ResourceResolverChain): String? {
                        return null
                    }

                    override fun resolveResource(request: HttpServletRequest?, requestPath: String, locations: MutableList<out Resource>, chain: ResourceResolverChain): Resource? {
                        val reportsDir: JavaPath = frontendDirs.getReportsDir()
                        val filePath: JavaPath = reportsDir.resolve(requestPath)

                        // security check: only allow to serve files inside the reports directory
                        if (isInvalidRequest(reportsDir, filePath)) {
                            return null
                        }

                        return FileSystemResource(filePath)
                    }
                }
        )

        // Without this, we get a warning: "Locations list is empty. No resources will be served unless a custom ResourceResolver is configured as an alternative to PathResourceResolver".
        // To make the warning go away, we are setting it to a classpath resource that doesn't exist.
        locations = listOf(
                ClassPathResource("/path-to-a-classpath-resource-that-doesn't exist-${UUID.randomUUID()}")
        )
    }

    private fun isInvalidRequest(reportsDir: JavaPath, filePath: JavaPath): Boolean = !isValidRequest(reportsDir, filePath)

    private fun isValidRequest(reportsDir: JavaPath,
                               filePath: JavaPath): Boolean {
        val reportsDirAbsoluteNormalizedPath: JavaPath = reportsDir.toAbsolutePath().normalize()
        val fileAbsoluteNormalizedPath: JavaPath = filePath.toAbsolutePath().normalize()

        return fileAbsoluteNormalizedPath.startsWith(reportsDirAbsoluteNormalizedPath)
    }

}
