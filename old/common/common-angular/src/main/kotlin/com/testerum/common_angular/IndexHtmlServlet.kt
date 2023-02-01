package com.testerum.common_angular

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class IndexHtmlServlet(private val indexHtmlContent: String) : HttpServlet() {

    companion object {
        private val BASE_URL_REGEX = Regex("""<base\s+href\s*=\s*"[^"]*">""", RegexOption.IGNORE_CASE)
    }

    override fun doGet(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val contextPath = request.contextPath
        val baseHref = if (contextPath == "") {
            "/"
        } else {
            "${contextPath}/"
        }
        val replacedIndexHtml: String = BASE_URL_REGEX.replace(
            indexHtmlContent,
            "<base href=\"${baseHref}\">"
        )

        response.outputStream.write(
            replacedIndexHtml.toByteArray(Charsets.UTF_8)
        )
    }

}
