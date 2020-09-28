package com.testerum.report_server

import com.testerum.common_angular.AngularForwarderFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.servlet.DispatcherType

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

}
