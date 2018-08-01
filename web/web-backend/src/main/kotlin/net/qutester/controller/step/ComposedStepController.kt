package net.qutester.controller.step

import net.qutester.model.infrastructure.path.CopyPath
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.infrastructure.path.RenamePath
import net.qutester.model.step.ComposedStepDef
import net.qutester.model.step.filter.StepsTreeFilter
import net.qutester.model.step.operation.UpdateComposedStepDef
import net.qutester.model.step.operation.response.CheckComposedStepDefUpdateCompatibilityResponse
import net.qutester.model.step.tree.ComposedContainerStepNode
import net.qutester.service.step.StepService
import net.qutester.service.step.StepUpdateCompatibilityService
import net.qutester.service.step.StepUpdateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/steps/composed")
class ComposedStepController(val stepService: StepService,
                             val stepUpdateService: StepUpdateService,
                             val stepUpdateCompatibilityService: StepUpdateCompatibilityService) {

    @RequestMapping (method = [(RequestMethod.POST)])
    @ResponseBody
    fun getComposedSteps(@RequestBody stepsTreeFilter: StepsTreeFilter): List<ComposedStepDef> {
        return stepService.getComposedSteps(stepsTreeFilter)
    }

    @RequestMapping (params = ["path"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun getComposedStepByPath(@RequestParam(value = "path") path:String): ResponseEntity<*> {
        val step: ComposedStepDef? = stepService.getComposedStepByPath(Path.createInstance(path))

        return if (step == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(step)
        }
    }

    @RequestMapping (params = ["path"], method = [(RequestMethod.DELETE)])
    fun delete(@RequestParam(value = "path") path:String) {
        stepService.remove(Path.createInstance(path))
    }

    @RequestMapping (path = ["/create"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun create(@RequestBody composedStepDef: ComposedStepDef): ComposedStepDef {
        return stepService.create(composedStepDef)
    }

    @RequestMapping (path = ["/update/check"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun updateCheckCompatibility(@RequestBody updateComposedStepDef: UpdateComposedStepDef): CheckComposedStepDefUpdateCompatibilityResponse {
        return stepUpdateCompatibilityService.checkUpdateCompatibility(updateComposedStepDef)
    }

    @RequestMapping (path = ["/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun update(@RequestBody composedStepDef: ComposedStepDef): ComposedStepDef {
        return stepUpdateService.update(composedStepDef)
    }

    @RequestMapping(path = ["/directory"], method = [(RequestMethod.PUT)])
    @ResponseBody
    fun renameDirectory(@RequestBody renamePath: RenamePath): Path {
        return stepService.renameDirectory(renamePath)
    }

    @RequestMapping(path = ["/directory"], method = [(RequestMethod.DELETE)])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        stepService.deleteDirectory(
                Path.createInstance(pathAsString)
        )
    }

    @RequestMapping(path = ["/directory/move"], method = [(RequestMethod.POST)])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        stepService.moveDirectoryOrFile(copyPath)
    }

    @RequestMapping("/directory-tree", method = [RequestMethod.GET])
    @ResponseBody
    fun getDirectoriesTree(): ComposedContainerStepNode {
        return stepService.getDirectoriesTree()
    }

    @RequestMapping(path = ["/warnings"], method = [RequestMethod.POST])
    @ResponseBody
    fun getWarnings(@RequestBody composedStepDef: ComposedStepDef): ComposedStepDef {
        return stepService.getWarnings(composedStepDef, keepExistingWarnings = false)
    }

}