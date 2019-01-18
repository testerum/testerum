package com.testerum.web_backend.filter

object AngularForwardingSelector {

    private val ignoredUrls: List<Regex> = listOf(
            // JavaScript
            matchesFileExtension(".js"),

            // CSS
            matchesFileExtension(".css"),

            // images
            matchesFileExtension(".ico"),
            matchesFileExtension(".png"),
            matchesFileExtension(".gif"),
            matchesFileExtension(".jpg"),
            matchesFileExtension(".jpeg"),
            matchesFileExtension(".svg"),

            // fonts
            matchesFileExtension(".ttf"),
            matchesFileExtension(".eot"),
            matchesFileExtension(".woff"),
            matchesFileExtension(".woff2"),

            // source maps
            matchesFileExtension(".map"),

            // REST web services
            Regex("^/rest/.*"),

            // version page
            Regex("^/version\\.html.*")
    )

    fun shouldForward(requestUri: String): Boolean = !shouldIgnore(requestUri)

    private fun shouldIgnore(requestUri: String): Boolean {
        return ignoredUrls.any {
            it.matches(requestUri)
        }
    }

    private fun matchesFileExtension(extension: String) = Regex(".*\\Q$extension\\E$")

}
