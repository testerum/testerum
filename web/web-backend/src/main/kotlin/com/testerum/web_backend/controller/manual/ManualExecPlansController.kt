package com.testerum.web_backend.controller.manual

import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualExecPlan
import com.testerum.model.manual.ManualExecPlans
import com.testerum.model.manual.runner.enums.ManualExecPlanStatus
import com.testerum.service.manual.ManualTestsService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/manual")
class ManualExecPlansController() {

    @RequestMapping(method = [RequestMethod.GET], path = ["plans"])
    @ResponseBody
    fun getManualExecPlans(): ManualExecPlans {
        val manualExecPlan = ManualExecPlan(
                Path.createInstance("/test/super"),
                null,
                "Acceptance",
                "1.1.2",
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
    }
}
