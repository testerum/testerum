package com.testerum.web_backend.controllers.features

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.model.feature.Feature
import com.testerum.model.feature.filter.FeaturesTreeFilter
import com.testerum.model.feature.tree.RootFeatureNode
import com.testerum.model.infrastructure.path.Path
import com.testerum.web_backend.services.features.FeaturesFrontendService
import com.testerum.web_backend.util.toFilesToUpload
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/features")
class FeatureController(private val featuresFrontendService: FeaturesFrontendService,
                        private val restApiObjectMapper: ObjectMapper) {

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

    @RequestMapping(method = [RequestMethod.POST], path = [""], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun save(@RequestParam("feature") featurePart: String,
             @RequestParam("attachmentsPathsToDelete") attachmentsPathsToDeletePart: String,
             @RequestParam("attachmentFiles") attachmentFiles: List<MultipartFile>): Feature {
        val feature = restApiObjectMapper.readValue<Feature>(featurePart)
        val attachmentsPathsToDelete = restApiObjectMapper.readValue<List<Path>>(attachmentsPathsToDeletePart)

        return featuresFrontendService.save(
                feature,
                attachmentsPathsToDelete,
                attachmentFiles.toFilesToUpload()
        )
    }

    @RequestMapping(method = [RequestMethod.DELETE], path = [""], params = ["path"])
    fun delete(@RequestParam(value = "path") path: String) {
        featuresFrontendService.delete(Path.createInstance(path))
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["attachments"], params = ["path"])
    @ResponseBody
    fun getAttachmentFileContent(@RequestParam(value = "path") pathAsString: String,
                                 @RequestParam(value = "thumbnail", required = false) thumbnail: Boolean,
                                 @RequestParam(value = "size", required = false) size: String? = null,
                                 response: HttpServletResponse) {
        featuresFrontendService.writeAttachmentFileContentToResponse(
                Path.createInstance(pathAsString),
                thumbnail,
                size,
                response
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/copy"])
    @ResponseBody
    fun copy(@RequestParam("sourcePath") sourcePath: String,
             @RequestParam("destinationPath") destinationPath: String): Path {

        return featuresFrontendService.copyFeatureOrTest(
                Path.createInstance(sourcePath),
                Path.createInstance(destinationPath)
        )
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/move"])
    @ResponseBody
    fun move(@RequestParam("sourcePath") sourcePath: String,
             @RequestParam("destinationPath") destinationPath: String): Path {

        return featuresFrontendService.moveFeatureOrTest(
                Path.createInstance(sourcePath),
                Path.createInstance(destinationPath)
        )
    }
}
