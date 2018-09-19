package com.testerum.web_backend.controllers.features

import com.testerum.model.feature.Feature
import com.testerum.model.feature.filter.FeaturesTreeFilter
import com.testerum.model.feature.tree.RootFeatureNode
import com.testerum.model.file.Attachment
import com.testerum.model.file.FileToUpload
import com.testerum.model.infrastructure.path.Path
import com.testerum.web_backend.services.features.FeaturesFrontendService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/features")
class FeatureController(private val featuresFrontendService: FeaturesFrontendService) {

    @RequestMapping(method = [RequestMethod.GET], path = [""])
    @ResponseBody
    fun getAllFeatures(): Collection<Feature> {
        return featuresFrontendService.getAllFeatures()
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["tree"])
    @ResponseBody
    fun getFeaturesTree(@RequestBody filter: FeaturesTreeFilter): RootFeatureNode {
        return featuresFrontendService.getFeaturesTree(filter)
    }

    @RequestMapping(method = [RequestMethod.GET], path = [""], params = ["path"])
    @ResponseBody
    fun getFeatureAtPath(@RequestParam(value = "path") path: String): Feature? {
        return featuresFrontendService.getFeatureAtPath(
                Path.createInstance(path)
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = [""])
    @ResponseBody
    fun save(@RequestBody feature: Feature): Feature {
        return featuresFrontendService.save(feature)
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        featuresFrontendService.delete(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/fileUpload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun uploadAttachments(@RequestParam(value = "path") featurePathAsString: String,
                          @RequestParam("files") multipartFiles: List<MultipartFile>): List<Attachment> {
        return featuresFrontendService.uploadAttachments(
                Path.createInstance(featurePathAsString),
                multipartFiles.toFilesToUpload()
        )
    }

    private fun List<MultipartFile>.toFilesToUpload(): List<FileToUpload> = map { it.toFileToUpload() }

    private fun MultipartFile.toFileToUpload() = FileToUpload(originalFilename, inputStream)

}
