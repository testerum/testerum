package com.testerum.web_backend.controllers.results

import com.testerum.web_backend.services.dirs.FrontendDirs
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
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
        cacheControl = CacheControl.empty()
                .mustRevalidate()

        resourceResolvers = listOf(
                object: ResourceResolver {
                    override fun resolveUrlPath(resourcePath: String, locations: MutableList<out Resource>, chain: ResourceResolverChain): String? {
                        return null
                    }

                    override fun resolveResource(request: HttpServletRequest?, requestPath: String, locations: MutableList<out Resource>, chain: ResourceResolverChain): Resource? {
                        val (projectId, path) = parseRequestPath(requestPath)

                        val reportsDir: JavaPath = frontendDirs.getReportsDir(projectId)
                        val filePath: JavaPath = reportsDir.resolve(path)

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

    private fun parseRequestPath(requestPath: String): Pair<String, String> {
        val indexOfFirstSlash = requestPath.indexOf('/')

        if (indexOfFirstSlash == -1) {
            throw IllegalArgumentException("invalid request path [$requestPath]: missing project id")
        }

        val projectId = StringUtils.substring(requestPath, 0, indexOfFirstSlash)
        val path = StringUtils.substring(requestPath, indexOfFirstSlash + 1)

        return Pair(projectId, path)
    }

    private fun isInvalidRequest(reportsDir: JavaPath, filePath: JavaPath): Boolean = !isValidRequest(reportsDir, filePath)

    private fun isValidRequest(reportsDir: JavaPath,
                               filePath: JavaPath): Boolean {
        val reportsDirAbsoluteNormalizedPath: JavaPath = reportsDir.toAbsolutePath().normalize()
        val fileAbsoluteNormalizedPath: JavaPath = filePath.toAbsolutePath().normalize()

        return fileAbsoluteNormalizedPath.startsWith(reportsDirAbsoluteNormalizedPath)
    }

}
