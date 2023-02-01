package com.testerum.report_server

import com.testerum.report_server.config.ReportServerConfig
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File

@Component
class ApplicationWebMvcConfigurer : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry
            .addResourceHandler("/static-reports/**")
            .addResourceLocations("file:${ReportServerConfig.getReportsRootDirectory()}${File.separator}")
    }

}
