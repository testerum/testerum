package com.testerum.web_backend.controller.manual

import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualExecPlan
import com.testerum.model.manual.ManualExecPlans
import com.testerum.model.manual.ManualTest
import com.testerum.model.manual.ManualTreeTest
import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.model.manual.enums.ManualTestStepStatus
import com.testerum.model.manual.runner.enums.ManualExecPlanStatus
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeBase
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeContainer
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeNode
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeRoot
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.step.StepDef
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.TextStepPatternPart
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/manual")
class ManualExecPlansController {

    companion object {
        private val LOG = LoggerFactory.getLogger(ManualExecPlansController::class.java)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["plans"])
    @ResponseBody
    fun getManualExecPlans(): ManualExecPlans {
        val manualExecPlan = ManualExecPlan(
                Path.createInstance("/test/super"),
                null,
                "Acceptance",
                "1.1.2",
                "Super Execution Plan",
                ManualExecPlanStatus.IN_EXECUTION,
                LocalDateTime.of(2018, 3, 21, 10, 0, 0),
                null,
                emptyList(),
                5,
                2,
                1,
                1,
                0,
                1
        )
        return ManualExecPlans(arrayListOf(manualExecPlan), emptyList())
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["plans"], params = ["path"])
    @ResponseBody
    fun getManualExecPlan(@RequestParam(value = "path") path: String): ManualExecPlan {
       return ManualExecPlan(
                Path.createInstance("/test/super"),
                null,
                "Acceptance",
                "1.1.2",
               "Super Execution Plan",
                ManualExecPlanStatus.IN_EXECUTION,
                LocalDateTime.of(2018, 3, 21, 10, 0, 0),
                null,
               listOf(
                       ManualTreeTest(
                               Path.createInstance("/home/ui/Home Page.test"),
                               "Home Page"
                       ),
                       ManualTreeTest(
                               Path.createInstance("/not existing/path/Unknown Test.test"),
                               "Unknown Test"
                       )
               ),
                5,
                2,
                1,
                1,
                0,
                1
        )
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["plans"], params = ["path"])
    fun deleteManualExecPlan(@RequestParam(value = "path") path: String) {
        LOG.warn("DELETE MANUAL EXEC", path)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["status_tree"], params = ["path"])
    @ResponseBody
    fun getManualTestsStatusTree(@RequestParam(value = "path") path: String): ManualTestsStatusTreeRoot {
        
        val dir11 = mutableListOf<ManualTestsStatusTreeBase>()
        dir11 += ManualTestsStatusTreeNode(Path.createInstance("/dir11_node1"), "dir11_node1", ManualTestStatus.PASSED);
        dir11 += ManualTestsStatusTreeNode(Path.createInstance("/dir11_node2"), "dir11_node2", ManualTestStatus.PASSED);
        dir11 += ManualTestsStatusTreeNode(Path.createInstance("/dir11_node3"), "dir11_node3", ManualTestStatus.PASSED);

        val dir12 = mutableListOf<ManualTestsStatusTreeBase>()
        dir12 += ManualTestsStatusTreeNode(Path.createInstance("/dir12_node1"), "dir12_node1", ManualTestStatus.PASSED);
        dir12 += ManualTestsStatusTreeNode(Path.createInstance("/dir12_node2"), "dir12_node2", ManualTestStatus.FAILED);
        dir12 += ManualTestsStatusTreeNode(Path.createInstance("/dir12_node3"), "dir12_node3", ManualTestStatus.BLOCKED);

        val rootChildren = mutableListOf<ManualTestsStatusTreeBase>()
        rootChildren += ManualTestsStatusTreeContainer(Path.createInstance("/dir11"), "dir11", ManualTestStatus.PASSED, dir11)
        rootChildren += ManualTestsStatusTreeContainer(Path.createInstance("/dir12"), "dir12", ManualTestStatus.FAILED, dir12)
        rootChildren += ManualTestsStatusTreeNode(Path.createInstance("/dir1_node1"), "dir1_node1", ManualTestStatus.NOT_EXECUTED);

        return ManualTestsStatusTreeRoot(Path.EMPTY, "", ManualTestStatus.FAILED, rootChildren)
    }


    @RequestMapping(method = [RequestMethod.GET], path = ["runner"], params = ["path"])
    @ResponseBody
    fun getManualRunnerTest(@RequestParam(value = "path") path: String): ManualTest {
        return ManualTest(
                Path.createInstance(path),
                Path.createInstance(path),
                "Create Owner Test",
                "This is a cool test",
                listOf("acasa", "love", "super"),
                listOf(ManualTestStepStatus.PASSED, ManualTestStepStatus.FAILED, ManualTestStepStatus.NOT_EXECUTED),
                listOf(
                        StepCall(
                                "1",
                                ComposedStepDef(
                                        Path.createInstance("acasa"),
                                        Path.createInstance("acasa"),
                                        StepPhaseEnum.GIVEN,
                                        StepPattern(
                                                listOf(
                                                        TextStepPatternPart("is the first step")
                                                )
                                        ),
                                        "fist step description",
                                        listOf("tag1", "tag2"),
                                        listOf(
                                                StepCall(
                                                        "bla",
                                                        BasicStepDef(
                                                                StepPhaseEnum.GIVEN,
                                                                StepPattern(
                                                                        listOf(
                                                                                TextStepPatternPart("is the first sub-step of first step")
                                                                        )
                                                                ),
                                                                className = "com.testerum.ClasaDePoveste",
                                                                methodName = "zmeulCelRau"
                                                        ),
                                                        listOf()
                                                ),
                                                StepCall(
                                                        "bla",
                                                        BasicStepDef(
                                                                StepPhaseEnum.GIVEN,
                                                                StepPattern(
                                                                        listOf(
                                                                                TextStepPatternPart("is the second sub-step of first step")
                                                                        )
                                                                ),
                                                                className = "com.testerum.ClasaDePoveste",
                                                                methodName = "zmeulCelRau"
                                                        ),
                                                        listOf()
                                                )
                                        )

                                ),
                                emptyList()
                        ),
                        StepCall(
                                "bla",
                                BasicStepDef(
                                        StepPhaseEnum.GIVEN,
                                        StepPattern(
                                                listOf(
                                                        TextStepPatternPart("is the second step")
                                                )
                                        ),
                                        className = "com.testerum.ClasaDePoveste",
                                        methodName = "zmeulCelRau"
                                ),
                                listOf()
                        ),
                        StepCall(
                                "bla",
                                BasicStepDef(
                                        StepPhaseEnum.GIVEN,
                                        StepPattern(
                                                listOf(
                                                        TextStepPatternPart("is the third step")
                                                )
                                        ),
                                        className = "com.testerum.ClasaDePoveste",
                                        methodName = "zmeulCelRau"
                                ),
                                listOf()
                        )
                ),
                ManualTestStatus.FAILED,
                "This is a comment, my comment!"
        )
    }
}
