package com.testerum.web_backend.services.home

import com.testerum.model.home.Home
import com.testerum.web_backend.services.project.ProjectFrontendService
import com.testerum.web_backend.services.version_info.VersionInfoFrontendService
import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.util.*

class HomeFrontendService(private val projectFrontendService: ProjectFrontendService,
                          private val versionInfoFrontendService: VersionInfoFrontendService) {

    companion object {
        private val QUOTES_CLASSPATH_LOCATION = "home-quotes.txt"

        private val QUOTES: List<String> = run {
            val classLoader = HomeFrontendService::class.java.classLoader

            val inputStream: InputStream = classLoader.getResourceAsStream(QUOTES_CLASSPATH_LOCATION)
                    ?: throw IllegalStateException("could not find classpath resource [$QUOTES_CLASSPATH_LOCATION]")

            val quotesContent = inputStream.use {
                IOUtils.toString(it, Charsets.UTF_8)
            }

            quotesContent.lines()
                    .filter { it.isNotBlank() }
        }

        private val RANDOM = Random()
    }

    fun getHomePageModel(): Home {
        return Home(
                quote = getRandomQuote(),
                testerumVersion = getTesterumVersion(),
                recentProjects = projectFrontendService.getProjects()
        )
    }

    private fun getRandomQuote(): String {
        val randomIndex = RANDOM.nextInt(QUOTES.size)

        return QUOTES[randomIndex]
    }

    private fun getTesterumVersion(): String = versionInfoFrontendService.getProjectVersion()

}
