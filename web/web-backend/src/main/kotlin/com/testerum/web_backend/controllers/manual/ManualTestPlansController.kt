package com.testerum.web_backend.controllers.manual

import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualStepCall
import com.testerum.model.manual.ManualTest
import com.testerum.model.manual.ManualTestPlan
import com.testerum.model.manual.ManualTestPlans
import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.model.manual.enums.ManualTestStepStatus
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeRoot
import com.testerum.model.manual.status_tree.filter.ManualTreeStatusFilter
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.web_backend.services.manual.ManualTestPlansFrontendService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manual")
class ManualTestPlansController(private val manualTestPlansFrontendService: ManualTestPlansFrontendService) {

    @RequestMapping(method = [RequestMethod.PUT], path = ["/plans"])
    @ResponseBody
    fun savePlan(@RequestBody manualTestPlan: ManualTestPlan): ManualTestPlan {
        return manualTestPlansFrontendService.savePlan(manualTestPlan)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["plans"])
    @ResponseBody
    fun getPlans(): ManualTestPlans {
        return manualTestPlansFrontendService.getPlans()
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["plans"], params = ["planPath"])
    @ResponseBody
    fun getPlanAtPath(@RequestParam(value = "planPath") planPath: String): ManualTestPlan? {
        return manualTestPlansFrontendService.getPlanAtPath(
                Path.createInstance(planPath)
        )
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["plans/finalize"], params = ["planPath"])
    @ResponseBody
    fun finalizePlan(@RequestParam(value = "planPath") planPath: String): ManualTestPlan {
        return manualTestPlansFrontendService.finalizePlan(
                Path.createInstance(planPath)
        )
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["plans/activate"], params = ["planPath"])
    @ResponseBody
    fun makePlanActive(@RequestParam(value = "planPath") planPath: String): ManualTestPlan {
        return manualTestPlansFrontendService.makePlanActive(
                Path.createInstance(planPath)
        )
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["plans"], params = ["planPath"])
    fun deleteManualTestPlan(@RequestParam(value = "planPath") planPath: String) {
        manualTestPlansFrontendService.deletePlanAtPath(
                Path.createInstance(planPath)
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["status_tree"], params = ["planPath"])
    @ResponseBody
    fun getTestsTreeAtPlanPath(@RequestParam(value = "planPath") planPath: String,
                               @RequestBody statusTreeFilter: ManualTreeStatusFilter): ManualTestsStatusTreeRoot {
        return manualTestPlansFrontendService.getTestsTreeAtPlanPath(
                Path.createInstance(planPath),
                statusTreeFilter
        )
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/plans/runner"], params = ["planPath", "testPath"])
    @ResponseBody
    fun getTestAtPath(@RequestParam(value = "planPath") planPath: String,
                      @RequestParam(value = "testPath") testPath: String): ManualTest? {
        return manualTestPlansFrontendService.getTestAtPath(
                Path.createInstance(planPath),
                Path.createInstance(testPath)
        ) 
    }

    @RequestMapping(method = [RequestMethod.PUT], path = ["/plans/runner"], params = ["planPath"])
    @ResponseBody
    fun updateManualTest(@RequestParam(value = "planPath") planPath: String,
                         @RequestBody manualTest: ManualTest): ManualTest {
        return ManualTest(
                manualTest.path,
                manualTest.path,
                "Sign-up",
                "This is a positive test for the sign-up functionality.",
                listOf("sign-up", "user"),
                listOf(
                        ManualStepCall(
                                stepCall = StepCall(
                                        "1",
                                        ComposedStepDef(
                                                Path.createInstance("signup"),
                                                Path.createInstance("signup"),
                                                StepPhaseEnum.GIVEN,
                                                StepPattern(
                                                        listOf(
                                                                TextStepPatternPart("I'm on the sign-up page")
                                                        )
                                                ),
                                                "",
                                                listOf("tag1", "tag2"),
                                                listOf()
                                        ),
                                        emptyList()
                                ),
                                status = ManualTestStepStatus.NOT_EXECUTED
                        ),
                        ManualStepCall(
                                stepCall = StepCall(
                                        "2",
                                        BasicStepDef(
                                                StepPhaseEnum.WHEN,
                                                StepPattern(
                                                        listOf(
                                                                TextStepPatternPart("I write the email address 'test@testerum.com'")
                                                        )
                                                ),
                                                className = "com.testerum.ClasaDePoveste",
                                                methodName = "zmeulCelRau"
                                        ),
                                        listOf()
                                ),
                                status = ManualTestStepStatus.NOT_EXECUTED
                        ),
                        ManualStepCall(
                                stepCall = StepCall(
                                        "3",
                                        BasicStepDef(
                                                StepPhaseEnum.WHEN,
                                                StepPattern(
                                                        listOf(
                                                                TextStepPatternPart("I write the password 'myPassword123'")
                                                        )
                                                ),
                                                className = "com.testerum.ClasaDePoveste",
                                                methodName = "zmeulCelRau"
                                        ),
                                        listOf()
                                ),
                                status = ManualTestStepStatus.NOT_EXECUTED
                        ),
                        ManualStepCall(
                                stepCall = StepCall(
                                        "4",
                                        BasicStepDef(
                                                StepPhaseEnum.WHEN,
                                                StepPattern(
                                                        listOf(
                                                                TextStepPatternPart("I press the SignUp button")
                                                        )
                                                ),
                                                className = "com.testerum.ClasaDePoveste",
                                                methodName = "zmeulCelRau"
                                        ),
                                        listOf()
                                ),
                                status = ManualTestStepStatus.NOT_EXECUTED
                        ),
                        ManualStepCall(
                                stepCall = StepCall(
                                        "5",
                                        BasicStepDef(
                                                StepPhaseEnum.THEN,
                                                StepPattern(
                                                        listOf(
                                                                TextStepPatternPart("A user is created in the database")
                                                        )
                                                ),
                                                className = "com.testerum.ClasaDePoveste",
                                                methodName = "zmeulCelRau"
                                        ),
                                        listOf()
                                ),
                                status = ManualTestStepStatus.NOT_EXECUTED
                        )
                ),
                ManualTestStatus.NOT_EXECUTED,
                "",
                false
        )
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/plans/runner/next"], params = ["planPath", "currentTestPath"])
    @ResponseBody
    fun getNextUnExecutedTest(@RequestParam(value = "planPath") planPath: String,
                              @RequestParam(value = "currentTestPath") currentTestPath: String): Path {
        return Path.createInstance(currentTestPath)
    }
}
