package com.testerum.report_generators.reports.report_model.template.custom_template

import com.testerum.common_kotlin.doesNotExist
import com.testerum.report_generators.reports.report_model.base.BaseReportModelExecutionListener
import com.testerum.report_generators.reports.report_model.template.instance.ReportInstance
import com.testerum.runner.cmdline.report_type.builder.EventListenerProperties
import java.nio.file.Paths
import java.util.regex.Matcher
import java.nio.file.Path as JavaPath

class CustomTemplateExecutionListener(private val properties: Map<String, String>) : BaseReportModelExecutionListener() {

    private val _destinationDirectory: JavaPath = run {
        val destinationDir = properties[EventListenerProperties.Pretty.DESTINATION_DIRECTORY]
            ?: throw IllegalArgumentException("missing required property ${EventListenerProperties.Pretty.DESTINATION_DIRECTORY}")

        val destinationDirPath = Paths.get(destinationDir)
        val normalizedDestinationDirPath = destinationDirPath.toAbsolutePath().normalize()

        return@run normalizedDestinationDirPath
    }

    override val destinationDirectory: JavaPath
        get() = _destinationDirectory

    override val formatted: Boolean
        get() = false

    override fun afterModelSavedToFile() {
        val templateDirectory = properties[EventListenerProperties.CustomTemplate.TEMPLATE_DIRECTORY]
            ?: throw IllegalArgumentException("missing required property ${EventListenerProperties.CustomTemplate.TEMPLATE_DIRECTORY}")


        val templateDirectoryPath = Paths.get(templateDirectory)
        val normalizedTemplateDirectoryPath = templateDirectoryPath.toAbsolutePath().normalize()

        if (normalizedTemplateDirectoryPath.doesNotExist) {
            throw IllegalArgumentException("the file [$templateDirectory] (resolved as [$normalizedTemplateDirectoryPath]) does not exist")
        }

        val dataModelPatternToReplace = Regex("<!--### START: testerumRunnerReportModel ### -->[\\s\\S]*<!--### END: testerumRunnerReportModel ### -->")
        val dataModelAsString = modelAsJsonString?: throw RuntimeException("Data Model should not be null")
        val dataModelPatternReplacement = Matcher.quoteReplacement("<script type='text/javascript'>\n    window.testerumRunnerReportModel = ${dataModelAsString};\n  </script>")

        ReportInstance.create(
            templateDirectory = normalizedTemplateDirectoryPath,
            destinationDirectory = destinationDirectory,
            dataModelPatternToReplace = dataModelPatternToReplace,
            dataModelPatternReplacement = dataModelPatternReplacement
        )
    }
}
