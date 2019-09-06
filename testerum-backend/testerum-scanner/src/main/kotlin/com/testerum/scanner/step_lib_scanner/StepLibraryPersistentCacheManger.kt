package com.testerum.scanner.step_lib_scanner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.testerum.api.annotations.hooks.AfterAllTests
import com.testerum.api.annotations.hooks.AfterEachTest
import com.testerum.api.annotations.hooks.BeforeAllTests
import com.testerum.api.annotations.hooks.BeforeEachTest
import com.testerum.api.annotations.settings.DeclareSetting
import com.testerum.api.annotations.settings.DeclareSettings
import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.annotations.steps.When
import com.testerum.api.annotations.util.annotationNullToRealNull
import com.testerum.api.test_context.settings.model.SettingDefinition
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.step.BasicStepDef
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.StepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.model.text.parts.param_meta.TypeMetaFactory
import com.testerum.scanner.step_lib_scanner.model.ScannerBasicStepLibrary
import com.testerum.scanner.step_lib_scanner.model.ScannerLibraryFile
import com.testerum.scanner.step_lib_scanner.model.StepLibrariesScanResult
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.ScannerStepPatternParserFactory
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.ParamSimpleBasicStepPatternPart
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.SimpleBasicStepPatternPart
import com.testerum.scanner.step_lib_scanner.step_pattern_parser.model.TextSimpleBasicStepPatternPart
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.scanners.MethodParameterScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.reflections.util.ConfigurationBuilder
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.util.Collections.emptyList
import java.util.Collections.emptyMap
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.nio.file.Path as JavaPath

class StepLibraryPersistentCacheManger(private val threadPool: ExecutorService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(StepLibraryPersistentCacheManger::class.java)

        private val OBJECT_MAPPER = ObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(KotlinModule())
            registerModule(JavaTimeModule())

            configure(SerializationFeature.INDENT_OUTPUT, false)
            configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
        }

        private val STEP_PATTERN_PARSER = ParserExecuter(ScannerStepPatternParserFactory.pattern())
    }

    fun scan(jars: List<JavaPath>, cacheFile: JavaPath): StepLibrariesScanResult {
        LOG.info("scanning ${jars.size} jars...")

        // todo: scan jars in parallel

        // todo: use a profiler to check why it's not instantaneous when jar files didn't change
        val startTime = System.nanoTime()

        val alreadyScannedFiles: Map<String, ScannerBasicStepLibrary> = parseExistingCache(cacheFile)

        val libraries = mutableListOf<ScannerBasicStepLibrary>()

        val jarUrls: Array<URL> = jars
                .filter { !it.toString().contains("testerum-api-") } // todo: remove this hack - run the scanner in a separate process and scan the classpath instead
                .map { it.toUri().toURL() }
                .toTypedArray()
        val classLoader: ClassLoader = URLClassLoader(jarUrls, StepLibraryPersistentCacheManger::class.java.classLoader)

        var changed = false
        for (jar in jars) {
            val libraryResult: StepLibraryResult = scanJar(jar, alreadyScannedFiles, classLoader)

            libraries += libraryResult.library

            if (libraryResult.changed) {
                changed = true
            }
        }

        val endTime = System.nanoTime()
        val timeTakenMillis = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)

        val scanResult = StepLibrariesScanResult(libraries)

        if (changed) {
            serializeCache(scanResult, cacheFile)
        }

        LOG.info("scanning ${jars.size} jars took $timeTakenMillis ms")

        return scanResult.copy(
                libraries = libraries.filter { it.isNotEmpty() }
        )
    }

    private fun parseExistingCache(cacheFile: JavaPath): Map<String, ScannerBasicStepLibrary> {
        if (!Files.exists(cacheFile)) {
            return emptyMap()
        }

        val scanResult: StepLibrariesScanResult
        try {
            scanResult = Files.newInputStream(cacheFile).use { inputStream ->
                OBJECT_MAPPER.readValue(inputStream, StepLibrariesScanResult::class.java)
            }
        } catch (e: Exception) {
            LOG.warn("failed to parse existing cache at [${cacheFile.toAbsolutePath()}]; the existing cache file will be deleted and a full scan will be performed")

            Files.delete(cacheFile)

            return emptyMap()
        }

        val alreadyScannedFiles = mutableMapOf<String, ScannerBasicStepLibrary>()
        for (library in scanResult.libraries) {
            val previousValue: ScannerBasicStepLibrary? = alreadyScannedFiles.put(library.jarFile.name, library)

            if (previousValue != null) {
                LOG.warn("detected duplicate library file [${library.jarFile.name}] in in cache at [${cacheFile.toAbsolutePath()}]; the jar file will be re-scanned")
                alreadyScannedFiles.remove(library.jarFile.name)
            }
        }

        return alreadyScannedFiles
    }

    private fun scanJar(jar: JavaPath,
                        alreadyScannedFiles: Map<String, ScannerBasicStepLibrary>,
                        classLoader: ClassLoader): StepLibraryResult {
        try {
            return tryToScanJar(jar, alreadyScannedFiles, classLoader)
        } catch (e: Exception) {
            throw RuntimeException("failed to scan [${jar.toAbsolutePath()}]", e)
        }
    }

    private fun tryToScanJar(jar: JavaPath,
                             alreadyScannedFiles: Map<String, ScannerBasicStepLibrary>,
                             classLoader: ClassLoader): StepLibraryResult {
        val alreadyScannedLibrary: ScannerBasicStepLibrary? = alreadyScannedFiles[jar.fileName.toString()]
        if (alreadyScannedLibrary != null) {
            val alreadyScannedFile: ScannerLibraryFile = alreadyScannedLibrary.jarFile

            if (alreadyScannedFile.sizeInBytes == Files.size(jar)) {
                if (alreadyScannedFile.modifiedTimestamp == Files.getLastModifiedTime(jar).toInstant()) {
                    return StepLibraryResult(alreadyScannedLibrary, changed = false)
                }
            }
        }

        val libraryFileFuture: Future<ScannerLibraryFile> = threadPool.submit(Callable {
            getLibraryFile(jar)
        })

        val annotationScanResultFuture: Future<AnnotationScanResult> = threadPool.submit(Callable {
            scanForAnnotations(jar, classLoader)
        })

        val annotationScanResult: AnnotationScanResult = annotationScanResultFuture.get()
        val library = ScannerBasicStepLibrary(
                jarFile = libraryFileFuture.get(),
                steps = annotationScanResult.steps,
                hooks = annotationScanResult.hooks,
                settingDefinitions = annotationScanResult.settings
        )

        return StepLibraryResult(library, changed = true)
    }

    private fun getLibraryFile(jar: JavaPath): ScannerLibraryFile {
        return ScannerLibraryFile(
                name = jar.fileName.toString(),
                sizeInBytes = Files.size(jar),
                modifiedTimestamp = Files.getLastModifiedTime(jar).toInstant()
        )
    }

    private fun scanForAnnotations(jar: JavaPath, classLoader: ClassLoader): AnnotationScanResult {
        val jarUrl: URL = jar.toUri().toURL()

        val configuration: ConfigurationBuilder = ConfigurationBuilder()
                .setUrls(jarUrl)
                .setScanners(
                        TypeAnnotationsScanner(),
                        SubTypesScanner(),
                        MethodAnnotationsScanner(),
                        MethodParameterScanner()
                )

        // todo: check that this doesn't cause a memory leak (maybe we should run the scanner in a separate process to prevent this)
        configuration.classLoaders = arrayOf(classLoader)

        val reflections = Reflections(configuration)

        val steps: List<BasicStepDef> = getSteps(reflections)
        val hooks: List<HookDef> = getHooks(reflections)
        val settings: List<SettingDefinition> = getSettings(reflections)

        return AnnotationScanResult(steps, hooks, settings)
    }

    private fun getSteps(reflections: Reflections): List<BasicStepDef> {
        val steps = mutableListOf<BasicStepDef>()

        // todo: good error messages when failing (jar, className, methodName, why it failed)

        addStepsForPhase(steps, reflections, Given::class.java)
        addStepsForPhase(steps, reflections, When::class.java)
        addStepsForPhase(steps, reflections, Then::class.java)

        return steps
    }

    private fun addStepsForPhase(destinationStepsList: MutableList<BasicStepDef>,
                                 reflections: Reflections,
                                 phaseAnnotationClass: Class<out Annotation>) {
        val methods: Set<Method> = reflections.getMethodsAnnotatedWith(phaseAnnotationClass)
        for (method in methods) {
            addStepsForMethod(destinationStepsList, phaseAnnotationClass, method)
        }
    }

    private fun addStepsForMethod(destinationStepsList: MutableList<BasicStepDef>, phaseAnnotationClass: Class<out Annotation>, method: Method) {
        try {
            tryToAddStepsForMethod(destinationStepsList, phaseAnnotationClass, method)
        } catch (e: Exception) {
            throw RuntimeException("failed to add steps for method [$method], annotation [$phaseAnnotationClass]", e)
        }
    }

    private fun tryToAddStepsForMethod(destinationStepsList: MutableList<BasicStepDef>, phaseAnnotationClass: Class<out Annotation>, method: Method) {
        val phaseAnnotation = StepPhaseAnnotation(
                method.getAnnotation(phaseAnnotationClass)
        )

        destinationStepsList += createStepFromMethod(phaseAnnotation, method)
    }

    private fun createStepFromMethod(phaseAnnotation: StepPhaseAnnotation,
                                     method: Method): BasicStepDef {
        try {
            return tryToCreateStepFromMethod(phaseAnnotation, method)
        } catch (e: Exception) {
            throw RuntimeException("failed to create step from method [$method]", e)
        }
    }

    private fun tryToCreateStepFromMethod(phaseAnnotation: StepPhaseAnnotation, method: Method): BasicStepDef {
        if (phaseAnnotation.pattern == "") {
            throw RuntimeException("empty pattern for method [$method]")
        }

        val simplePatternParts: List<SimpleBasicStepPatternPart> = STEP_PATTERN_PARSER.parse(phaseAnnotation.pattern)

        val scannerParts = mutableListOf<StepPatternPart>()

        val methodParameters: Array<out Parameter> = method.parameters
        val parametersCountFromPattern = simplePatternParts.count { it is ParamSimpleBasicStepPatternPart }
        if (parametersCountFromPattern != methodParameters.size) {
            throw RuntimeException("parameter count mismatch: pattern [${phaseAnnotation.pattern}] has $parametersCountFromPattern parameters, but the method has ${methodParameters.size} parameters")
        }

        var paramIndex = -1

        for (i in 0 until simplePatternParts.size) {
            val simplePart = simplePatternParts[i]

            val scannerPart: StepPatternPart = when (simplePart) {
                is TextSimpleBasicStepPatternPart -> TextStepPatternPart(text = simplePart.text)

                is ParamSimpleBasicStepPatternPart -> {
                    val paramName = simplePart.name

                    paramIndex++
                    val param = methodParameters[paramIndex]

                    // todo: move this to a separate method
                    val enumValues: List<String>

                    val enumConstants: Array<out Any>? = param.type.enumConstants
                    enumValues = enumConstants?.map { (it as Enum<*>).name }
                                              ?.toList()
                                              ?: emptyList()

                    val paramAnnotation: Param? = param.getAnnotation(Param::class.java)

                    ParamStepPatternPart(
                            name = paramName,
                            typeMeta = TypeMetaFactory.getTypeMetaFromJavaType(param.type.name, enumValues),
                            description = getParamDescription(paramAnnotation)
                    )
                }
            }

            scannerParts += scannerPart
        }

        return BasicStepDef(
                phase = phaseAnnotation.phase,
                stepPattern = StepPattern(scannerParts),
                className = method.declaringClass.name,
                methodName = method.name,
                description = phaseAnnotation.description,
                tags = phaseAnnotation.tags
        )
    }

    private fun serializeCache(scanResult: StepLibrariesScanResult, destinationCacheFile: JavaPath) {
        Files.createDirectories(destinationCacheFile.parent)

        OBJECT_MAPPER.writeValue(
                destinationCacheFile.toFile(),
                scanResult
        )
    }

    private fun getParamDescription(paramAnnotation: Param?): String? {
        if (paramAnnotation == null) {
            return null
        }

        return paramAnnotation.description.annotationNullToRealNull()
    }

    private fun getHooks(reflections: Reflections): List<HookDef> {
        val hooks = mutableListOf<HookDef>()

        addHooksForPhase(hooks, reflections, BeforeAllTests::class.java)
        addHooksForPhase(hooks, reflections, BeforeEachTest::class.java)
        addHooksForPhase(hooks, reflections, AfterEachTest::class.java)
        addHooksForPhase(hooks, reflections, AfterAllTests::class.java)

        return hooks
    }

    private fun addHooksForPhase(destinationHooksList: MutableList<HookDef>, reflections: Reflections, phaseAnnotationClass: Class<out Annotation>) {
        val methods: Set<Method> = reflections.getMethodsAnnotatedWith(phaseAnnotationClass)
        for (method in methods) {
            addHooksForMethod(destinationHooksList, phaseAnnotationClass, method)
        }
    }

    private fun addHooksForMethod(destinationHooksList: MutableList<HookDef>, phaseAnnotationClass: Class<out Annotation>, method: Method) {
        try {
            tryToAddHooksForMethod(destinationHooksList, phaseAnnotationClass, method)
        } catch (e: Exception) {
            throw RuntimeException("failed to add hooks for method [$method], annotation [$phaseAnnotationClass]", e)
        }
    }

    private fun tryToAddHooksForMethod(destinationHooksList: MutableList<HookDef>, phaseAnnotationClass: Class<out Annotation>, method: Method) {
        val phaseAnnotation = HookPhaseAnnotation(
                method.getAnnotation(phaseAnnotationClass)
        )

        destinationHooksList += createHookFromMethod(phaseAnnotation, method)
    }

    private fun createHookFromMethod(phaseAnnotation: HookPhaseAnnotation, method: Method): HookDef {
        try {
            return tryToCreateHookFromMethod(phaseAnnotation, method)
        } catch (e: Exception) {
            throw RuntimeException("failed to create step from method [$method]", e)
        }

    }

    private fun tryToCreateHookFromMethod(phaseAnnotation: HookPhaseAnnotation, method: Method): HookDef {
        return HookDef(
                phase = phaseAnnotation.phase,
                className = method.declaringClass.name,
                methodName = method.name,
                order = phaseAnnotation.order,
                description = phaseAnnotation.description
        )
    }

    private fun getSettings(reflections: Reflections): List<SettingDefinition> {
        // todo: extract method

        val annotatedClasses = mutableSetOf<Class<*>>()

        annotatedClasses += reflections.getTypesAnnotatedWith(DeclareSettings::class.java)
        annotatedClasses += reflections.getTypesAnnotatedWith(DeclareSetting::class.java)

        val result = mutableListOf<SettingDefinition>()

        for (annotatedClass in annotatedClasses) {
            // this method will also "see through" the "@DeclareSettings" container and properly return the nested @DeclareSetting annotations
            val annotations: Array<out DeclareSetting> = annotatedClass.getAnnotationsByType(DeclareSetting::class.java)

            for (annotation in annotations) {
                result += annotation.toSetting()
            }
        }

        return result
    }

    private fun DeclareSetting.toSetting() = SettingDefinition(key, label, type, defaultValue, enumValues.toList(), description, category)

    private class StepPhaseAnnotation(annotation: Annotation) {
        private val _phase: StepPhaseEnum = when (annotation) {
            is Given -> StepPhaseEnum.GIVEN
            is When  -> StepPhaseEnum.WHEN
            is Then  -> StepPhaseEnum.THEN
            else     -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
        }
        private val _pattern: String = when (annotation) {
            is Given -> annotation.value
            is When  -> annotation.value
            is Then  -> annotation.value
            else     -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
        }
        private val _description: String? = when (annotation) {
            is Given -> annotation.description.annotationNullToRealNull()
            is When  -> annotation.description.annotationNullToRealNull()
            is Then  -> annotation.description.annotationNullToRealNull()
            else     -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
        }
        private val _tags: List<String> = when (annotation) {
            is Given -> annotation.tags.toList()
            is When  -> annotation.tags.toList()
            is Then  -> annotation.tags.toList()
            else     -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
        }


        val phase: StepPhaseEnum = _phase
        val pattern: String = _pattern
        val description: String? = _description
        val tags: List<String> = _tags
    }

    private class HookPhaseAnnotation(annotation: Annotation) {
        private val _phase: HookPhase = when (annotation) {
            is BeforeAllTests -> HookPhase.BEFORE_ALL_TESTS
            is BeforeEachTest -> HookPhase.BEFORE_EACH_TEST
            is AfterEachTest  -> HookPhase.AFTER_EACH_TEST
            is AfterAllTests  -> HookPhase.AFTER_ALL_TESTS
            else     -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
        }

        private val _description: String? = when (annotation) {
            is BeforeAllTests -> annotation.description.annotationNullToRealNull()
            is BeforeEachTest -> annotation.description.annotationNullToRealNull()
            is AfterEachTest  -> annotation.description.annotationNullToRealNull()
            is AfterAllTests  -> annotation.description.annotationNullToRealNull()
            else     -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
        }

        private val _order: Int = when (annotation) {
            is BeforeAllTests -> annotation.order
            is BeforeEachTest -> annotation.order
            is AfterEachTest  -> annotation.order
            is AfterAllTests  -> annotation.order
            else     -> throw IllegalArgumentException("unsupported annotation [${annotation.annotationClass}]")
        }

        val phase: HookPhase = _phase
        val description: String? = _description
        val order: Int = _order
    }

    private data class AnnotationScanResult(val steps: List<BasicStepDef>,
                                            val hooks: List<HookDef>,
                                            val settings: List<SettingDefinition>)

    private data class StepLibraryResult(val library: ScannerBasicStepLibrary,
                                         val changed: Boolean)

}
