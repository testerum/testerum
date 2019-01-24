package com.testerum.web_backend.services.home

import com.testerum.model.home.Home
import com.testerum.web_backend.services.project.ProjectFrontendService
import com.testerum.web_backend.services.version_info.VersionInfoFrontendService

class HomeFrontendService(private val quotesService: QuotesService,
                          private val versionInfoFrontendService: VersionInfoFrontendService,
                          private val projectFrontendService: ProjectFrontendService) {

    fun getHomePageModel(): Home {
        return Home(
                quote = quotesService.getRandomQuote(),
                testerumVersion = versionInfoFrontendService.getProjectVersion(),
                recentProjects = projectFrontendService.getProjects()
        )
    }

}
