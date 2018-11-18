package com.testerum.web_backend.controllers.manual

import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualTest
import com.testerum.model.manual.ManualTestPlan
import com.testerum.model.manual.ManualTestPlans
import com.testerum.model.manual.ManualTreeTest
import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.model.manual.enums.ManualTestStepStatus
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeBase
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeContainer
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeNode
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeRoot
import com.testerum.model.manual.status_tree.filter.ManualTreeStatusFilter
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.TextStepPatternPart
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/manual")
class ManualTestPlansController {

    companion object {
        private val LOG = LoggerFactory.getLogger(ManualTestPlansController::class.java)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["plans"])
    @ResponseBody
    fun getManualExecPlans(): ManualTestPlans {
        val manualExecPlan = ManualTestPlan(
                Path.createInstance("/signup"),
                null,
                "Full regression (acceptance 1.1.2)",
                "",
                false,
                LocalDateTime.of(2018, 3, 21, 10, 0, 0),
                null,
                emptyList(),
                2,
                1,
                1,
                0,
                1
        )
        return ManualTestPlans(arrayListOf(manualExecPlan), emptyList())
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["plans"], params = ["planPath"])
    @ResponseBody
    fun getManualExecPlan(@RequestParam(value = "planPath") planPath: String): ManualTestPlan {
       return ManualTestPlan(
                Path.createInstance("/signup"),
                null,
               "Full regression (acceptance 1.1.2)",
               "",
               false,
                LocalDateTime.of(2018, 3, 21, 10, 0, 0),
                null,
               listOf(
                       ManualTreeTest(
                               Path.createInstance("/signup/Signup.test"),
                               "Sign-up"
                       ),
                       ManualTreeTest(
                               Path.createInstance("/signup/SignupWithoutPassword.test"),
                               "Sign-up without password"
                       )
               ),
                2,
                1,
                1,
                0,
                1
        )
    }

    @RequestMapping(method = [RequestMethod.PUT], path = ["/plans"])
    @ResponseBody
    fun updateManualTest(@RequestBody manualTestPlan: ManualTestPlan): ManualTestPlan {
        return this.getManualExecPlan(manualTestPlan.path.toString())
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["plans/finalize"], params = ["planPath"])
    @ResponseBody
    fun finalizeManualExecPlan(@RequestParam(value = "planPath") planPath: String): ManualTestPlan {
        return this.getManualExecPlan(planPath)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["plans/bringBackInExecution"], params = ["planPath"])
    @ResponseBody
    fun bringBackInExecutionManualExecPlan(@RequestParam(value = "planPath") planPath: String): ManualTestPlan {
        return this.getManualExecPlan(planPath)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["plans"], params = ["planPath"])
    fun deleteManualExecPlan(@RequestParam(value = "planPath") planPath: String) {
        LOG.warn("DELETE MANUAL EXEC", planPath)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["status_tree"], params = ["planPath"])
    @ResponseBody
    fun getManualTestsStatusTree(@RequestParam(value = "planPath") planPath: String,
                                 @RequestBody statusTreeFilter: ManualTreeStatusFilter): ManualTestsStatusTreeRoot {
        LOG.debug("treeFilter: "+ statusTreeFilter.toString())

        val dir11 = mutableListOf<ManualTestsStatusTreeBase>()
        dir11 += ManualTestsStatusTreeNode(Path.createInstance("/signup/Signup.test"), "Sign-up", ManualTestStatus.IN_PROGRESS);
        dir11 += ManualTestsStatusTreeNode(Path.createInstance("/signup/SignupWithoutPassword.test"), "Sign-up without password", ManualTestStatus.PASSED);
        dir11 += ManualTestsStatusTreeNode(Path.createInstance("/signup/SignupWithoutEmail.test"), "Sign-up without email", ManualTestStatus.NOT_EXECUTED);

        val dir12 = mutableListOf<ManualTestsStatusTreeBase>()
        dir12 += ManualTestsStatusTreeNode(Path.createInstance("/dir12_node1"), "Create User", ManualTestStatus.PASSED);
        dir12 += ManualTestsStatusTreeNode(Path.createInstance("/dir12_node1"), "Get User", ManualTestStatus.PASSED);
        dir12 += ManualTestsStatusTreeNode(Path.createInstance("/dir12_node2"), "Update User", ManualTestStatus.FAILED);
        dir12 += ManualTestsStatusTreeNode(Path.createInstance("/dir12_node3"), "Delete User", ManualTestStatus.BLOCKED);

        val rootChildren = mutableListOf<ManualTestsStatusTreeBase>()
        rootChildren += ManualTestsStatusTreeContainer(Path.createInstance("/signup"), "sign-up", ManualTestStatus.PASSED, dir11)
        rootChildren += ManualTestsStatusTreeContainer(Path.createInstance("/users"), "users", ManualTestStatus.FAILED, dir12)
        rootChildren += ManualTestsStatusTreeContainer(Path.createInstance("/authorization"), "Authorization", ManualTestStatus.NOT_EXECUTED, mutableListOf<ManualTestsStatusTreeBase>());

        return ManualTestsStatusTreeRoot(Path.EMPTY, "", ManualTestStatus.FAILED, rootChildren)
    }


    @RequestMapping(method = [RequestMethod.GET], path = ["/plans/runner"], params = ["planPath", "testPath"])
    @ResponseBody
    fun getManualRunnerTest(@RequestParam(value = "planPath") planPath: String,
                            @RequestParam(value = "testPath") testPath: String): ManualTest {
        return ManualTest(
                Path.createInstance(testPath),
                Path.createInstance(testPath),
                "Sign-up",
                "This is a positive test for the sign-up functionality.",
                listOf("sign-up", "user"),
                listOf(
                        ManualTestStepStatus.NOT_EXECUTED,
                        ManualTestStepStatus.NOT_EXECUTED,
                        ManualTestStepStatus.NOT_EXECUTED,
                        ManualTestStepStatus.NOT_EXECUTED,
                        ManualTestStepStatus.NOT_EXECUTED),
                listOf(
                        StepCall(
                                "1",
                                ComposedStepDef(
                                        Path.createInstance("/signup"),
                                        Path.createInstance("/signup"),
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
                        StepCall(
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
                        StepCall(
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
                        StepCall(
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
                        StepCall(
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
                        )
                ),
                ManualTestStatus.NOT_EXECUTED,
                "",
                false
        )
    }


    @RequestMapping(method = [RequestMethod.PUT], path = ["/plans/runner"], params = ["planPath"])
    @ResponseBody
    fun updateManualTest(@RequestParam(value = "planPath") planPath: String,
                         @RequestBody manualTest: ManualTest): ManualTest {
        return getManualRunnerTest(planPath, manualTest.path.toString());
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/plans/runner/next"], params = ["planPath", "currentTestPath"])
    @ResponseBody
    fun getNextUnExecutedTest(@RequestParam(value = "planPath") planPath: String,
                              @RequestParam(value = "currentTestPath") currentTestPath: String): Path {
        return Path.createInstance(currentTestPath);
    }
}
