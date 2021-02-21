package com.testerum.report_generators.reports.report_model.template.instance

import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Path as JavaPath

object ReportInstance {
    private val LOG = LoggerFactory.getLogger(ReportInstance::class.java)

    fun create(
        templateDirectory: java.nio.file.Path,
        destinationDirectory: java.nio.file.Path,
        dataModelPatternToReplace: Regex,
        dataModelPatternReplacement: String
    ) {
        copyTemplateFiles(templateDirectory, destinationDirectory)
        addDataModelToTemplate(destinationDirectory, dataModelPatternToReplace, dataModelPatternReplacement)
    }

    private fun copyTemplateFiles(templateDirectory: JavaPath, destinationDirectory: JavaPath) {
        FileUtils.copyDirectory(templateDirectory.toFile(), destinationDirectory.toFile())
    }

    private fun addDataModelToTemplate(destinationDirectory: JavaPath, dataModelPatternToReplace: Regex, dataModelPatternReplacement: String) {
        val indexFilePath = destinationDirectory.resolve("index.html")
        LOG.debug("replacing model in [${indexFilePath}]")

        val indexFile = File(indexFilePath.toString())

        val originalContent = indexFile.readText(Charsets.UTF_8)
        val replacedContent = originalContent.replace(
            dataModelPatternToReplace,
            dataModelPatternReplacement
        )

        indexFile.writeText(replacedContent, Charsets.UTF_8)
    }
}
