package net.qutester.controller.feature

import net.qutester.model.feature.Feature
import net.qutester.model.feature.filter.FeaturesTreeFilter
import net.qutester.model.file.Attachment
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.main_tree.RootMainNode
import net.qutester.service.feature.FeatureService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/features")
class FeatureController(private val featureService: FeatureService) {

    @RequestMapping (path = ["tree"], method = [RequestMethod.POST])
    @ResponseBody
    fun getFeaturesTree(@RequestBody featuresTreeFilter: FeaturesTreeFilter): RootMainNode {
        return featureService.getFeaturesTree(featuresTreeFilter)
    }

    @RequestMapping (method = [RequestMethod.GET])
    @ResponseBody
    fun getAllFeatures(): List<Feature> {
        return featureService.getAllFeatures()
    }

    @RequestMapping (params = ["path"], method = [RequestMethod.GET])
    @ResponseBody
    fun getFeatureAtPath(@RequestParam(value = "path") path:String): Feature? {
        return featureService.getFeatureAtPath(Path.createInstance(path))
    }

    @RequestMapping (params = ["path"], method = [RequestMethod.DELETE])
    fun delete(@RequestParam(value = "path") path:String) {
        featureService.remove(Path.createInstance(path))
    }

    @RequestMapping (method = [RequestMethod.POST])
    @ResponseBody
    fun save(@RequestBody feature: Feature): Feature {
        return featureService.save(feature)
    }

    @RequestMapping (
            path = ["/fileUpload"],
            params = ["path"],
            method = [RequestMethod.POST],
            consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun uploadImages(@RequestParam(value = "path") featurePathAsString:String,
                     @RequestParam("files") uploadingFiles: Array<MultipartFile>): List<Attachment> {
        return featureService.uploadFiles(
                Path.createInstance(featurePathAsString),
                uploadingFiles
        )
    }
}