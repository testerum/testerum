package com.testerum.report_server

import com.testerum.common_angular.AngularForwarderFilter
import com.testerum.common_angular.IndexHtmlServlet
import com.testerum.common_kotlin.contentOfClasspathResourceAt
import javax.servlet.DispatcherType
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfig {

    @Bean
    fun angularFilter() = FilterRegistrationBean<AngularForwarderFilter>().apply {
        filter = AngularForwarderFilter(
            forwardToUrl = "/index.html",
            customIgnoredUrls = listOf(
                // REST web services
                Regex("^/v1/.*"),

                // static files of reports
                Regex("^/static-reports/.*"),

                // actuator endpoints
                Regex("^/management/.*")
            )
        )

        addUrlPatterns("/*")

        setDispatcherTypes(DispatcherType.REQUEST)
    }

    @Bean
    fun indexHtmlServlet() = ServletRegistrationBean<IndexHtmlServlet>().apply {
        servlet = IndexHtmlServlet(
            indexHtmlContent = contentOfClasspathResourceAt("frontend/index.html")
        )

        addUrlMappings("/index.html")
    }

}
