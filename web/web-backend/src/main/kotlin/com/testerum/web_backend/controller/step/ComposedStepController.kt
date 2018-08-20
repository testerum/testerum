package com.testerum.web_backend.controller.step

import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.operation.response.CheckComposedStepDefUpdateCompatibilityResponse
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.service.save.SaveService
import com.testerum.service.step.StepCache
import com.testerum.service.step.StepUpdateCompatibilityService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/steps/composed")
class ComposedStepController(private val stepCache: StepCache,
                             private val stepUpdateCompatibilityService: StepUpdateCompatibilityService,
                             private val saveService: SaveService) {

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun getComposedSteps(@RequestBody stepsTreeFilter: StepsTreeFilter): List<ComposedStepDef> {
        return stepCache.getComposedSteps(stepsTreeFilter)
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getComposedStepByPath(@RequestParam(value = "path") path: String): ResponseEntity<*> {
        val step: ComposedStepDef? = stepCache.getComposedStepAtPath(Path.createInstance(path))

        return if (step == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(step)
        }
    }

    @RequestMapping(method = [RequestMethod.DELETE], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        stepCache.removeComposedStep(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/update/check"])
    @ResponseBody
    fun updateCheckCompatibility(@RequestBody composedStepDef: ComposedStepDef): CheckComposedStepDefUpdateCompatibilityResponse {
        return stepUpdateCompatibilityService.checkUpdateCompatibility(composedStepDef)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/save"])
    @ResponseBody
    fun save(@RequestBody composedStepDef: ComposedStepDef): ComposedStepDef {
        return saveService.saveComposedStep(composedStepDef)
    }

    @RequestMapping(method = [RequestMethod.PUT], path = ["/directory"])
    @ResponseBody
    fun renameDirectory(@RequestBody renamePath: RenamePath): Path {
        return stepCache.renameComposedStepDirectory(renamePath)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["/directory"])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        stepCache.deleteComposedStepDirectory(
                Path.createInstance(pathAsString)
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/directory/move"])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        stepCache.moveComposedStepDirectoryOrFile(copyPath)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/directory-tree"])
    @ResponseBody
    fun getDirectoriesTree(): ComposedContainerStepNode {
        return stepCache.getDirectoriesTree()
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/warnings"])
    @ResponseBody
    fun getWarnings(@RequestBody composedStepDef: ComposedStepDef): ComposedStepDef {
        return stepCache.getWarnings(composedStepDef, keepExistingWarnings = false)
    }

}
