package com.testerum.file_service.file

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.serializing.Serializer
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.deleteRecursivelyIfExists
import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.getContent
import com.testerum.common_kotlin.list
import com.testerum.common_kotlin.smartMoveTo
import com.testerum.file_service.file.util.escape
import com.testerum.file_service.mapper.business_to_file.manual.BusinessToFileManualTestPlanMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualTestPlanMapper
import com.testerum.model.exception.ValidationException
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualTestPlan
import com.testerum.model.manual.ManualTestPlans
import com.testerum.test_file_format.manual_test_plan.FileManualTestPlan
import com.testerum.test_file_format.manual_test_plan.FileManualTestPlanParserFactory
import com.testerum.test_file_format.manual_test_plan.FileManualTestPlanSerializer
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.nio.file.Path as JavaPath

class ManualTestPlanFileService(private val businessToFileManualTestPlanMapper: BusinessToFileManualTestPlanMapper,
                                private val fileToBusinessManualTestPlanMapper: FileToBusinessManualTestPlanMapper) {

    companion object {
        private const val MANUAL_TEST_PLAN_FILE_NAME = "info.manual_test_plan"

        private val PLAN_PARSER: ParserExecuter<FileManualTestPlan> = ParserExecuter(FileManualTestPlanParserFactory.manualTestPlan())
        private val PLAN_SERIALIZER: Serializer<FileManualTestPlan> = FileManualTestPlanSerializer
    }

    fun save(plan: ManualTestPlan,
             manualTestsDir: JavaPath): ManualTestPlan {
        val oldEscapedPath = plan.oldPath?.escape()
        val newEscapedPath = plan.getNewPath().escape()

        val oldPlanDir: JavaPath? = oldEscapedPath?.let {
            manualTestsDir.resolve(
                    it.directories.joinToString(separator = "/")
            ).toAbsolutePath().normalize()
        }
        val newPlanDir: JavaPath = manualTestsDir.resolve(
                newEscapedPath.directories.joinToString(separator = "/")
        ).toAbsolutePath().normalize()

        // handle rename
        oldPlanDir?.smartMoveTo(
                newPlanDir,
                createDestinationExistsException = {
                    val planDirPath = newEscapedPath.copy(fileName = null, fileExtension = null)

                    ValidationException(
                            globalMessage = "The plan at path [$planDirPath] already exists",
                            globalHtmlMessage = "The plan at path<br/><code>$planDirPath</code><br/>already exists"
                    )
                }
        )

        // write the new feature file
        newPlanDir.createDirectories()

        val filePlan = businessToFileManualTestPlanMapper.mapPlan(plan)
        val serializedFilePlan = PLAN_SERIALIZER.serializeToString(filePlan)

        val newPlanFile = newPlanDir.resolve(MANUAL_TEST_PLAN_FILE_NAME)

        newPlanFile.parent?.createDirectories()

        Files.write(
                newPlanFile,
                serializedFilePlan.toByteArray(Charsets.UTF_8),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )

        val newPath = Path.createInstance(
                manualTestsDir.relativize(newPlanDir).toString()
        ).escape()

        return plan.copy(
                path = newPath,
                oldPath = newPath
        )
    }

    fun getPlans(manualTestsDir: JavaPath): ManualTestPlans {
        val plansJavaPaths = manualTestsDir.list()

        val plans = plansJavaPaths.map { planJavaPath ->
            val planPath = Path.createInstance(
                    manualTestsDir.relativize(planJavaPath).toString()
            )

            getPlanAtPath(planPath, manualTestsDir)
        }

        val groupedPlans = plans.filterNotNull().groupBy { it.isFinalized }

        val activePlans = groupedPlans[false] ?: emptyList()
        val finalizedPlans = groupedPlans[true] ?: emptyList()

        return ManualTestPlans(activePlans, finalizedPlans)
    }

    fun getPlanAtPath(planPath: Path, manualTestsDir: JavaPath): ManualTestPlan? {
        val escapedPlanPath = planPath.escape()

        val planDir: JavaPath = manualTestsDir.resolve(
                escapedPlanPath.withoutFile().toString()
        )

        val planFile: JavaPath = planDir.resolve(MANUAL_TEST_PLAN_FILE_NAME)

        if (planFile.doesNotExist) {
            return null
        }

        val filePlan = PLAN_PARSER.parse(
                planFile.getContent()
        )

        val relativePlanPath: JavaPath = manualTestsDir.relativize(planDir)
        val plan = fileToBusinessManualTestPlanMapper.mapPlan(filePlan, relativePlanPath)

        return plan
    }

    fun deleteAtPath(planPath: Path, manualTestsDir: JavaPath) {
        val escapedPlanPath = planPath.escape()

        val planDir: JavaPath = manualTestsDir.resolve(
                escapedPlanPath.withoutFile().toString()
        )

        planDir.deleteRecursivelyIfExists()
    }

}