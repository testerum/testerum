package net.qutester.controller.step

import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.operation.UpdateComposedStepDef
import com.testerum.model.step.operation.response.CheckComposedStepDefUpdateCompatibilityResponse
import com.testerum.model.step.tree.ComposedContainerStepNode
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

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun getComposedSteps(@RequestBody stepsTreeFilter: StepsTreeFilter): List<ComposedStepDef> {
        return stepService.getComposedSteps(stepsTreeFilter)
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getComposedStepByPath(@RequestParam(value = "path") path: String): ResponseEntity<*> {
        val step: ComposedStepDef? = stepService.getComposedStepByPath(Path.createInstance(path))

        return if (step == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(step)
        }
    }

    @RequestMapping(method = [RequestMethod.DELETE], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        stepService.remove(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/create"])
    @ResponseBody
    fun create(@RequestBody composedStepDef: ComposedStepDef): ComposedStepDef {
        return stepService.create(composedStepDef)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/update/check"])
    @ResponseBody
    fun updateCheckCompatibility(@RequestBody updateComposedStepDef: UpdateComposedStepDef): CheckComposedStepDefUpdateCompatibilityResponse {
        return stepUpdateCompatibilityService.checkUpdateCompatibility(updateComposedStepDef)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/update"])
    @ResponseBody
    fun update(@RequestBody composedStepDef: ComposedStepDef): ComposedStepDef {
        return stepUpdateService.update(composedStepDef)
    }

    @RequestMapping(method = [RequestMethod.PUT], path = ["/directory"])
    @ResponseBody
    fun renameDirectory(@RequestBody renamePath: RenamePath): Path {
        return stepService.renameDirectory(renamePath)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["/directory"])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        stepService.deleteDirectory(
                Path.createInstance(pathAsString)
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/directory/move"])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        stepService.moveDirectoryOrFile(copyPath)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/directory-tree"])
    @ResponseBody
    fun getDirectoriesTree(): ComposedContainerStepNode {
        return stepService.getDirectoriesTree()
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/warnings"])
    @ResponseBody
    fun getWarnings(@RequestBody composedStepDef: ComposedStepDef): ComposedStepDef {
        return stepService.getWarnings(composedStepDef, keepExistingWarnings = false)
    }

}