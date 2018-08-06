package com.testerum.scanner_it_test

import com.testerum.api.annotations.hooks.HooksConstants
import com.testerum.api.test_context.settings.model.Setting
import com.testerum.api.test_context.settings.model.SettingType
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.step.BasicStepDef
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.scanner.step_lib_scanner.StepLibraryCacheManger
import com.testerum.scanner.step_lib_scanner.model.ScannerBasicStepScanResult
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StepLibraryCacheMangerTest {

    // note: because this test expects jar files, and because IntelliJ doesn't create them,
    // you will need to run the following before running this class from IntelliJ:
    //
    // mvn clean package -am -pl testerum-scanner-it/testerum-scanner-it-test

    // todo: tests for scanner that updates an existing cache

    private lateinit var threadPool: ExecutorService
    private lateinit var stepLibraryCacheManger: StepLibraryCacheManger

    @BeforeEach
    fun beforeAll() {
        // todo: test performance: does using more threads help (it may, if the whole stuff is IO bound)
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)
        stepLibraryCacheManger = StepLibraryCacheManger(threadPool)
    }

    @AfterEach
    fun afterAll() {
        threadPool.shutdownNow()
        // no need for threadPool.awaitExecution() - assuming StepLibraryCacheManger properly consumed all threads that it started
    }

    @Test
    fun `test from scratch - cache file doesn't exist`() {
        val stepLib1: Path = findWithWildcards("../testerum-scanner-it-steplib1/target/", "testerum-scanner-it-steplib1-*.jar")
        val stepLib2: Path = findWithWildcards("../testerum-scanner-it-steplib2/target/", "testerum-scanner-it-steplib2-*.jar")
        val stepLib3Java: Path = findWithWildcards("../testerum-scanner-it-steplib3-java/target/", "testerum-scanner-it-steplib3-java-*.jar")


        val threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)
        try {
            val cacheFile = Files.createTempFile("testerum-scanner-cache-it-", ".json")
            cacheFile.toFile()
                    .deleteOnExit()

            val stepScanResult: ScannerBasicStepScanResult = stepLibraryCacheManger.scan(
                    jars = listOf(stepLib1, stepLib2, stepLib3Java),
                    cacheFile = cacheFile
            )

            val libraries = stepScanResult.libraries
            assertThat(libraries, hasSize(3))

            // library 1
            val lib1 = libraries[0]
            assertThat(lib1.jarFile.name, matchesPattern("""testerum-scanner-it-steplib1-.*\.jar"""))

            val lib1_sortedSteps = lib1.steps.sortedWith(
                    compareBy(
                            { it.className },
                            { it.phase },
                            { it.methodName }
                    )
            )

            assertThat(lib1_sortedSteps, hasSize(13))
            assertThat(
                    lib1_sortedSteps[0],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.GIVEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "another step")
                                            )
                                    ),
                                    description = null,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MyOtherSteps",
                                    methodName = "givenAnotherStep"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[1],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.GIVEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "step with an "),
                                                    ParamStepPatternPart(
                                                            name = "enumParameter",
                                                            type = "java.util.concurrent.TimeUnit",
                                                            description = null,
                                                            enumValues = listOf("NANOSECONDS", "MICROSECONDS", "MILLISECONDS", "SECONDS", "MINUTES", "HOURS", "DAYS")
                                                    )
                                            )
                                    ),
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "givenAStepWithAnEnumParameter"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[2],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.GIVEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "a simple step")
                                            )
                                    ),
                                    description = null,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "givenSimpleStep"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[3],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.GIVEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "a step with all annotation fields")
                                            )
                                    ),
                                    description = "given description",
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "givenStepWithAllAnnotationFields"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[4],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.GIVEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "I login as "),
                                                    ParamStepPatternPart(
                                                            name = "username",
                                                            type = "java.lang.String",
                                                            description = null,
                                                            enumValues = emptyList()
                                                    ),
                                                    TextStepPatternPart(text = "/"),
                                                    ParamStepPatternPart(
                                                            name = "password",
                                                            type = "java.lang.String",
                                                            description = null,
                                                            enumValues = emptyList()
                                                    ),
                                                    TextStepPatternPart(text = " to "),
                                                    ParamStepPatternPart(
                                                            name = "host",
                                                            type = "java.lang.String",
                                                            description = null,
                                                            enumValues = emptyList()
                                                    ),
                                                    TextStepPatternPart(text = "/"),
                                                    ParamStepPatternPart(
                                                            name = "port",
                                                            type = "int",
                                                            description = "param description",
                                                            enumValues = emptyList()
                                                    )
                                            )
                                    ),
                                    description = null,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "givenStepWithParameters"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[5],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.WHEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "step with an "),
                                                    ParamStepPatternPart(
                                                            name = "enumParameter",
                                                            type = "java.util.concurrent.TimeUnit",
                                                            description = null,
                                                            enumValues = listOf("NANOSECONDS", "MICROSECONDS", "MILLISECONDS", "SECONDS", "MINUTES", "HOURS", "DAYS")
                                                    )
                                            )
                                    ),
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "whenAStepWithAnEnumParameter"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[6],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.WHEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "a simple step")
                                            )
                                    ),
                                    description = null,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "whenSimpleStep"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[7],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.WHEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "a step with all annotation fields")
                                            )
                                    ),
                                    description = "when description",
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "whenStepWithAllAnnotationFields"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[8],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.WHEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "I login as "),
                                                    ParamStepPatternPart(
                                                            name = "username",
                                                            type = "java.lang.String",
                                                            description = null,
                                                            enumValues = emptyList()
                                                    ),
                                                    TextStepPatternPart(text = "/"),
                                                    ParamStepPatternPart(
                                                            name = "password",
                                                            type = "java.lang.String",
                                                            description = null,
                                                            enumValues = emptyList()
                                                    ),
                                                    TextStepPatternPart(text = " to "),
                                                    ParamStepPatternPart(
                                                            name = "host",
                                                            type = "java.lang.String",
                                                            description = null,
                                                            enumValues = emptyList()
                                                    ),
                                                    TextStepPatternPart(text = "/"),
                                                    ParamStepPatternPart(
                                                            name = "port",
                                                            type = "int",
                                                            description = "param description",
                                                            enumValues = emptyList()
                                                    )
                                            )
                                    ),
                                    description = null,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "whenStepWithParameters"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[9],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.THEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "step with an "),
                                                    ParamStepPatternPart(
                                                            name = "enumParameter",
                                                            type = "java.util.concurrent.TimeUnit",
                                                            description = null,
                                                            enumValues = listOf("NANOSECONDS", "MICROSECONDS", "MILLISECONDS", "SECONDS", "MINUTES", "HOURS", "DAYS")
                                                    )
                                            )
                                    ),
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "thenAStepWithAnEnumParameter"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[10],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.THEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "a simple step")
                                            )
                                    ),
                                    description = null,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "thenSimpleStep"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[11],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.THEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "a step with all annotation fields")
                                            )
                                    ),
                                    description = "then description",
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "thenStepWithAllAnnotationFields"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSteps[12],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.THEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "I login as "),
                                                    ParamStepPatternPart(
                                                            name = "username",
                                                            type = "java.lang.String",
                                                            description = null,
                                                            enumValues = emptyList()
                                                    ),
                                                    TextStepPatternPart(text = "/"),
                                                    ParamStepPatternPart(
                                                            name = "password",
                                                            type = "java.lang.String",
                                                            description = null,
                                                            enumValues = emptyList()
                                                    ),
                                                    TextStepPatternPart(text = " to "),
                                                    ParamStepPatternPart(
                                                            name = "host",
                                                            type = "java.lang.String",
                                                            description = null,
                                                            enumValues = emptyList()
                                                    ),
                                                    TextStepPatternPart(text = "/"),
                                                    ParamStepPatternPart(
                                                            name = "port",
                                                            type = "int",
                                                            description = "param description",
                                                            enumValues = emptyList()
                                                    )
                                            )
                                    ),
                                    description = null,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "thenStepWithParameters"
                            )
                    )
            )


            val lib1_sortedHooks = lib1.hooks.sortedWith(
                    compareBy(
                            { it.className },
                            { it.phase },
                            { it.methodName }
                    )
            )

            assertThat(lib1_sortedHooks, hasSize(12))

            assertThat(
                    lib1_sortedHooks[0],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.BEFORE_ALL_TESTS,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "beforeAllTestsSimple",
                                    order = HooksConstants.DEFAULT_ORDER,
                                    description = ""
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[1],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.BEFORE_ALL_TESTS,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "beforeAllTestsWithAllAnnotationFields",
                                    order = 100,
                                    description = "beforeAllTests with all annotation fields"
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[2],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.BEFORE_ALL_TESTS,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "beforeAllTestsWithDescription",
                                    order = HooksConstants.DEFAULT_ORDER,
                                    description = "beforeAllTests description"
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[3],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.BEFORE_EACH_TEST,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "beforeTestSimple",
                                    order = HooksConstants.DEFAULT_ORDER,
                                    description = ""
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[4],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.BEFORE_EACH_TEST,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "beforeTestWithAllAnnotationFields",
                                    order = 100,
                                    description = "beforeTest with all annotation fields"
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[5],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.BEFORE_EACH_TEST,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "beforeTestWithDescription",
                                    order = HooksConstants.DEFAULT_ORDER,
                                    description = "beforeTest description"
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[6],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.AFTER_EACH_TEST,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "afterTestSimple",
                                    order = HooksConstants.DEFAULT_ORDER,
                                    description = ""
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[7],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.AFTER_EACH_TEST,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "afterTestWithAllAnnotationFields",
                                    order = 100,
                                    description = "afterTest with all annotation fields"
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[8],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.AFTER_EACH_TEST,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "afterTestWithDescription",
                                    order = HooksConstants.DEFAULT_ORDER,
                                    description = "afterTest description"
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[9],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.AFTER_ALL_TESTS,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "afterAllTestSimple",
                                    order = HooksConstants.DEFAULT_ORDER,
                                    description = ""
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[10],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.AFTER_ALL_TESTS,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "afterAllTestWithAllAnnotationFields",
                                    order = 100,
                                    description = "afterAllTest with all annotation fields"
                            )
                    )
            )
            assertThat(
                    lib1_sortedHooks[11],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.AFTER_ALL_TESTS,
                                    className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                                    methodName = "afterAllTestWithDescription",
                                    order = HooksConstants.DEFAULT_ORDER,
                                    description = "afterAllTest description"
                            )
                    )
            )

            val lib1_sortedSettings = lib1.settings.sortedBy { it.key }

            assertThat(lib1_sortedSettings, hasSize(4))

            assertThat(
                    lib1_sortedSettings[0],
                    equalTo(
                            Setting(
                                    key = "stepLib1.other",
                                    type = SettingType.TEXT,
                                    defaultValue = "yep",
                                    description = "other description",
                                    category = "other category"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSettings[1],
                    equalTo(
                            Setting(
                                    key = "stepLib1.param1",
                                    type = SettingType.NUMBER,
                                    defaultValue = "10",
                                    description = "param1 description",
                                    category = "param1 category"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSettings[2],
                    equalTo(
                            Setting(
                                    key = "stepLib1.param2",
                                    type = SettingType.TEXT,
                                    defaultValue = "some text",
                                    description = "param2 description",
                                    category = "param2 category"
                            )
                    )
            )
            assertThat(
                    lib1_sortedSettings[3],
                    equalTo(
                            Setting(
                                    key = "stepLib1.param3",
                                    type = SettingType.FILESYSTEM_DIRECTORY,
                                    defaultValue = "/some/path",
                                    description = "param3 description",
                                    category = "param3 category"
                            )
                    )
            )


            // library 2
            val lib2 = libraries[1]

            assertThat(lib2.jarFile.name, matchesPattern("""testerum-scanner-it-steplib2-.*\.jar"""))

            val lib2_sortedSteps = lib2.steps.sortedWith(
                    compareBy(
                            { it.className },
                            { it.phase },
                            { it.methodName }
                    )
            )

            assertThat(lib2_sortedSteps, hasSize(1))

            assertThat(
                    lib2_sortedSteps[0],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.GIVEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "a step in lib2")
                                            )
                                    ),
                                    description = null,
                                    className = "com.testerum.scanner_it_steplib2.steps.StepLib2MySteps",
                                    methodName = "givenAStepInLib2"
                            )
                    )
            )

            // library 3
            val lib3 = libraries[2]
            assertThat(lib3.jarFile.name, matchesPattern("""testerum-scanner-it-steplib3-.*\.jar"""))

            val lib3_sortedSteps = lib3.steps.sortedWith(
                    compareBy(
                            { it.className },
                            { it.phase },
                            { it.methodName }
                    )
            )

            assertThat(lib3_sortedSteps, hasSize(1))

            assertThat(
                    lib3_sortedSteps[0],
                    equalTo(
                            BasicStepDef(
                                    phase = StepPhaseEnum.GIVEN,
                                    stepPattern = StepPattern(
                                            patternParts = listOf(
                                                    TextStepPatternPart(text = "a step")
                                            )
                                    ),
                                    description = null,
                                    className = "com.testerum.scanner_it_steplib3java.steps.StepLib3JavaSteps",
                                    methodName = "aStep"
                            )
                    )
            )

            val lib3_sortedHooks = lib3.hooks.sortedWith(
                    compareBy(
                            { it.className },
                            { it.phase },
                            { it.methodName }
                    )
            )

            assertThat(lib3_sortedHooks, hasSize(1))

            assertThat(
                    lib3_sortedHooks[0],
                    equalTo(
                            HookDef(
                                    phase = HookPhase.BEFORE_EACH_TEST,
                                    className = "com.testerum.scanner_it_steplib3java.steps.StepLib3JavaSteps",
                                    methodName = "beforeEachTest",
                                    order = HooksConstants.DEFAULT_ORDER,
                                    description = ""
                            )
                    )
            )

            val lib3_sortedSettings = lib3.settings.sortedBy { it.key }

            assertThat(lib3_sortedSettings, hasSize(4))

            assertThat(
                    lib3_sortedSettings[0],
                    equalTo(
                            Setting(
                                    key = "stepLib3Java.param1",
                                    type = SettingType.NUMBER,
                                    defaultValue = "10",
                                    description = "param1 description",
                                    category = "param1 category"
                            )
                    )
            )
            assertThat(
                    lib3_sortedSettings[1],
                    equalTo(
                            Setting(
                                    key = "stepLib3Java.param2",
                                    type = SettingType.TEXT,
                                    defaultValue = "defVal",
                                    description = "param2 description",
                                    category = "param2 category"
                            )
                    )
            )
            assertThat(
                    lib3_sortedSettings[2],
                    equalTo(
                            Setting(
                                    key = "stepLib3Java.param3",
                                    type = SettingType.NUMBER,
                                    defaultValue = "13",
                                    description = "param3 description",
                                    category = "param3 category"
                            )
                    )
            )
            assertThat(
                    lib3_sortedSettings[3],
                    equalTo(
                            Setting(
                                    key = "stepLib3Java.param4",
                                    type = SettingType.TEXT,
                                    defaultValue = "aa",
                                    description = "param4 description",
                                    category = "param4 category"
                            )
                    )
            )

        } finally {
            threadPool.shutdownNow()
            // no need for threadPool.awaitExecution() because the try block insures that all threads finished
        }

    }

    private fun findWithWildcards(directory: String, wildcard: String): Path {
        return Files.newDirectoryStream(Paths.get(directory), wildcard).use { stream ->
            stream.first()
        }
    }

}