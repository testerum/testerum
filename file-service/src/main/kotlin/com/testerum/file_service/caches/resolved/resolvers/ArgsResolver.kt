package com.testerum.file_service.caches.resolved.resolvers

import com.testerum.common_kotlin.emptyToNull
import com.testerum.file_service.caches.resolved.resolvers.file_arg_transformer.FileArgTransformer
import com.testerum.file_service.file.ResourceFileService
import com.testerum.model.arg.Arg
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.resources.ResourceContext
import com.testerum.model.resources.ResourceType
import com.testerum.model.step.StepDef
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.warning.Warning
import java.nio.file.Path as JavaPath

class ArgsResolver(private val resourceFileService: ResourceFileService) {

    fun resolveArgs(args: List<Arg>, resolvedStepDef: StepDef, resourcesDir: JavaPath): List<Arg> {
        val result = mutableListOf<Arg>()

        val paramParts: List<ParamStepPatternPart> = resolvedStepDef.stepPattern.getParamStepPattern()

        for ((i: Int, arg: Arg) in args.withIndex()) {
            val paramPart: ParamStepPatternPart = paramParts[i]

            result += resolveArg(arg, paramPart, resourcesDir)
        }

        return result
    }

    private fun resolveArg(arg: Arg, paramPart: ParamStepPatternPart, resourcesDir: JavaPath): Arg {
        // 2. load resource if text starts with "file:" (ignore if it's escaped)
        var content: String? = arg.content
        var path: Path? = null
        val warnings = mutableListOf<Warning>()

        if (content != null) {
            content = when {
                content.startsWith("\\file:") -> content.substring(1)
                content.startsWith("file:")   -> {
                    val pathAsString: String = content.removePrefix("file:")

                    val resourceType: ResourceType? = ResourceType.getByFileName(pathAsString)
                    if (resourceType == null) {
                        warnings += Warning.externalResourceOfUnknownType(pathAsString)

                        null
                    } else {
                        path = Path.createInstance(pathAsString)

                        val resource: ResourceContext? = resourceFileService.getResourceByPathAndType(path, resourceType, resourcesDir)
                        if (resource == null) {
                            warnings += Warning.externalResourceNotFound("${resourceType.relativeRootDir}/$pathAsString")
                            println("=====================> cannot find resource [$pathAsString]")

                            null
                        } else {
                            FileArgTransformer.fileFormatToJson(resource.body, resourceType.javaType)
                        }
                    }
                }
                else -> arg.content
            }
        }

        val transformedContent = FileArgTransformer.fileFormatToJson(content.orEmpty(), paramPart.type).emptyToNull()

        return arg.copy(
                content = transformedContent,
                type = paramPart.type,
                path = path,
                oldPath = path,
                warnings = warnings
        )
    }

}
