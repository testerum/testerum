package com.testerum.web_backend.controller.manual

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualExecPlan
import com.testerum.model.manual.ManualExecPlans
import com.testerum.model.manual.ManualTreeTest
import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.model.manual.runner.enums.ManualExecPlanStatus
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeBase
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeContainer
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeNode
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeRoot
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
}
