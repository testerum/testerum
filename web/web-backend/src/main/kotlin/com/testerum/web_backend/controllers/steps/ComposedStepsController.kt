package com.testerum.web_backend.controllers.steps

import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.operation.response.CheckComposedStepDefUpdateCompatibilityResponse
import com.testerum.model.step.operation.response.CheckComposedStepDefUsageResponse
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.web_backend.services.steps.ComposedStepsFrontendService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/steps/composed")
class ComposedStepsController(private val composedStepsFrontendService: ComposedStepsFrontendService) {

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun getComposedSteps(@RequestBody filter: StepsTreeFilter): List<ComposedStepDef> {
        return composedStepsFrontendService.getComposedSteps(filter)
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getComposedStepAtPath(@RequestParam(value = "path") path: String): ResponseEntity<*> {
        val step: ComposedStepDef? = composedStepsFrontendService.getComposedStepAtPath(
                Path.createInstance(path)
        )

        return if (step == null) {
            ResponseEntity.notFound().build<ResponseEntity<*>>()
        } else {
            ResponseEntity.ok(step)
        }
    }

    @RequestMapping(method = [RequestMethod.DELETE], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        composedStepsFrontendService.deleteComposedStep(
                Path.createInstance(path)
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/update/check"])
    @ResponseBody
    fun updateCheck(@RequestBody composedStepDef: ComposedStepDef): CheckComposedStepDefUpdateCompatibilityResponse {
        return composedStepsFrontendService.checkUpdateCompatibility(composedStepDef)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/usage/check"])
    @ResponseBody
    fun usageCheck(@RequestBody composedStepDef: ComposedStepDef): CheckComposedStepDefUsageResponse {
        return composedStepsFrontendService.checkUsage(composedStepDef)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/save"])
    @ResponseBody
    fun save(@RequestBody composedStepDef: ComposedStepDef): ComposedStepDef {
        return composedStepsFrontendService.saveComposedStep(composedStepDef)
    }

    @RequestMapping(method = [RequestMethod.PUT], path = ["/directory"])
    @ResponseBody
    fun renameDirectory(@RequestBody renamePath: RenamePath): Path {
        return composedStepsFrontendService.renameComposedStepDirectory(renamePath)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = ["/directory"])
    fun deleteDirectory(@RequestParam("path") pathAsString: String) {
        composedStepsFrontendService.deleteComposedStepDirectory(
                Path.createInstance(pathAsString)
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/directory/move"])
    fun moveDirectoryOrFile(@RequestBody copyPath: CopyPath) {
        composedStepsFrontendService.moveComposedStepDirectoryOrFile(copyPath)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/directory-tree"])
    @ResponseBody
    fun getDirectoriesTree(): ComposedContainerStepNode {
        return composedStepsFrontendService.getDirectoriesTree()
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/warnings"])
    @ResponseBody
    fun getWarnings(@RequestBody composedStep: ComposedStepDef): ComposedStepDef {
        return composedStepsFrontendService.getWarnings(composedStep)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/copy"])
    @ResponseBody
    fun copy(@RequestParam("sourcePath") sourcePath: String,
             @RequestParam("destinationPath") destinationDirPath: String): Path {

        return composedStepsFrontendService.copyComposedStep(
                Path.createInstance(sourcePath),
                Path.createInstance(destinationDirPath)
        );
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/move"])
    @ResponseBody
    fun move(@RequestParam("sourcePath") sourcePath: String,
             @RequestParam("destinationPath") destinationDirPath: String): Path {

        return composedStepsFrontendService.moveComposedStep(
                Path.createInstance(sourcePath),
                Path.createInstance(destinationDirPath)
        );
    }
}
