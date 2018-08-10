package com.testerum.web_backend.controller.feature

import com.testerum.model.feature.Feature
import com.testerum.model.feature.filter.FeaturesTreeFilter
import com.testerum.model.feature.tree.RootFeatureNode
import com.testerum.model.file.Attachment
import com.testerum.model.infrastructure.path.Path
import com.testerum.service.feature.FeatureService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/features")
class FeatureController(private val featureService: FeatureService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getAllFeatures(): List<Feature> {
        return featureService.getAllFeatures()
    }

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun save(@RequestBody feature: Feature): Feature {
        return featureService.save(feature)
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getFeatureAtPath(@RequestParam(value = "path") path: String): Feature? {
        return featureService.getFeatureAtPath(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        featureService.remove(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["tree"])
    @ResponseBody
    fun getFeaturesTree(@RequestBody featuresTreeFilter: FeaturesTreeFilter): RootFeatureNode {
        return featureService.getFeaturesTree(featuresTreeFilter)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/fileUpload"], params = ["path", "files"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun uploadImages(@RequestParam(value = "path") featurePathAsString: String,
                     @RequestParam("files") uploadingFiles: Array<MultipartFile>): List<Attachment> {
        return featureService.uploadFiles(
                Path.createInstance(featurePathAsString),
                uploadingFiles
        )
    }

}
