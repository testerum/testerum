package com.testerum.scanner_it_test

import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.step.BasicStepDef
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.model.text.parts.param_meta.EnumTypeMeta
import com.testerum.model.text.parts.param_meta.NumberTypeMeta
import com.testerum.model.text.parts.param_meta.ObjectTypeMeta
import com.testerum.model.text.parts.param_meta.StringTypeMeta
import com.testerum.scanner.step_lib_scanner.ExtensionsScanner
import com.testerum.scanner.step_lib_scanner.model.ExtensionsScanConfig
import com.testerum.scanner.step_lib_scanner.model.ExtensionsScanResult
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import com.testerum.scanner.step_lib_scanner.model.hooks.HookPhase
import com.testerum_api.testerum_steps_api.annotations.hooks.HooksConstants
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingDefinition
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ExtensionsScannerTest {

    // note: because this test expects jar files, and because IntelliJ doesn't create them,
    // you will need to run the following before running this class from IntelliJ:
    //
    // mvn clean package -am -pl testerum-scanner-it/testerum-scanner-it-test

    // todo: tests for scanner that updates an existing cache

    private lateinit var threadPool: ExecutorService
    private lateinit var extensionsScanner: ExtensionsScanner

    @BeforeEach
    fun beforeAll() {
        // todo: test performance: does using more threads help (it may, if the whole stuff is IO bound)
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)
        extensionsScanner = ExtensionsScanner(threadPool)
    }

    @AfterEach
    fun afterAll() {
        threadPool.shutdownNow()
        // no need for threadPool.awaitExecution() - assuming StepLibraryPersistentCacheManger properly consumed all threads that it started
    }

    @Test
    fun test() {
        val threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1)
        try {
            val stepScanResult: ExtensionsScanResult = extensionsScanner.scan(
                ExtensionsScanConfig(
                    onlyFromPackages = listOf(
                        "com.testerum.scanner_it_steplib1.steps",
                        "com.testerum.scanner_it_steplib2.steps",
                        "com.testerum.scanner_it_steplib3java.steps"
                    )
                )
            )

            val sortedSteps = stepScanResult.steps.sortedWith(
                compareBy(
                    { it.className },
                    { it.phase },
                    { it.methodName }
                )
            )

            assertThat(sortedSteps, hasSize(15))
            assertThat(
                sortedSteps[0],
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
                        methodName = "givenAnotherStep",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[1],
                equalTo(
                    BasicStepDef(
                        phase = StepPhaseEnum.GIVEN,
                        stepPattern = StepPattern(
                            patternParts = listOf(
                                TextStepPatternPart(text = "step with an "),
                                ParamStepPatternPart(
                                    name = "enumParameter",
                                    typeMeta = EnumTypeMeta(
                                        "java.util.concurrent.TimeUnit",
                                        listOf("NANOSECONDS", "MICROSECONDS", "MILLISECONDS", "SECONDS", "MINUTES", "HOURS", "DAYS")
                                    ),
                                    description = null
                                )
                            )
                        ),
                        className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                        methodName = "givenAStepWithAnEnumParameter",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[2],
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
                        methodName = "givenSimpleStep",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[3],
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
                        methodName = "givenStepWithAllAnnotationFields",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[4],
                equalTo(
                    BasicStepDef(
                        phase = StepPhaseEnum.GIVEN,
                        stepPattern = StepPattern(
                            patternParts = listOf(
                                TextStepPatternPart(text = "I login as "),
                                ParamStepPatternPart(
                                    name = "username",
                                    typeMeta = StringTypeMeta(),
                                    description = null
                                ),
                                TextStepPatternPart(text = "/"),
                                ParamStepPatternPart(
                                    name = "password",
                                    typeMeta = StringTypeMeta(),
                                    description = null
                                ),
                                TextStepPatternPart(text = " to "),
                                ParamStepPatternPart(
                                    name = "host",
                                    typeMeta = StringTypeMeta(),
                                    description = null
                                ),
                                TextStepPatternPart(text = "/"),
                                ParamStepPatternPart(
                                    name = "port",
                                    typeMeta = NumberTypeMeta("int"),
                                    description = "param description"
                                )
                            )
                        ),
                        description = null,
                        className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                        methodName = "givenStepWithParameters",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[5],
                equalTo(
                    BasicStepDef(
                        phase = StepPhaseEnum.WHEN,
                        stepPattern = StepPattern(
                            patternParts = listOf(
                                TextStepPatternPart(text = "step with an "),
                                ParamStepPatternPart(
                                    name = "enumParameter",
                                    typeMeta = EnumTypeMeta(
                                        "java.util.concurrent.TimeUnit",
                                        listOf("NANOSECONDS", "MICROSECONDS", "MILLISECONDS", "SECONDS", "MINUTES", "HOURS", "DAYS")
                                    ),
                                    description = null
                                )
                            )
                        ),
                        className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                        methodName = "whenAStepWithAnEnumParameter",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[6],
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
                        methodName = "whenSimpleStep",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[7],
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
                        methodName = "whenStepWithAllAnnotationFields",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[8],
                equalTo(
                    BasicStepDef(
                        phase = StepPhaseEnum.WHEN,
                        stepPattern = StepPattern(
                            patternParts = listOf(
                                TextStepPatternPart(text = "I login as "),
                                ParamStepPatternPart(
                                    name = "username",
                                    typeMeta = StringTypeMeta(),
                                    description = null
                                ),
                                TextStepPatternPart(text = "/"),
                                ParamStepPatternPart(
                                    name = "password",
                                    typeMeta = StringTypeMeta(),
                                    description = null
                                ),
                                TextStepPatternPart(text = " to "),
                                ParamStepPatternPart(
                                    name = "host",
                                    typeMeta = StringTypeMeta(),
                                    description = null
                                ),
                                TextStepPatternPart(text = "/"),
                                ParamStepPatternPart(
                                    name = "port",
                                    typeMeta = NumberTypeMeta("int"),
                                    description = "param description"
                                )
                            )
                        ),
                        description = null,
                        className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                        methodName = "whenStepWithParameters",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[9],
                equalTo(
                    BasicStepDef(
                        phase = StepPhaseEnum.THEN,
                        stepPattern = StepPattern(
                            patternParts = listOf(
                                TextStepPatternPart(text = "step with an "),
                                ParamStepPatternPart(
                                    name = "enumParameter",
                                    typeMeta = EnumTypeMeta(
                                        "java.util.concurrent.TimeUnit",
                                        listOf("NANOSECONDS", "MICROSECONDS", "MILLISECONDS", "SECONDS", "MINUTES", "HOURS", "DAYS")
                                    ),
                                    description = null
                                )
                            )
                        ),
                        className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                        methodName = "thenAStepWithAnEnumParameter",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[10],
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
                        methodName = "thenSimpleStep",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[11],
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
                        methodName = "thenStepWithAllAnnotationFields",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[12],
                equalTo(
                    BasicStepDef(
                        phase = StepPhaseEnum.THEN,
                        stepPattern = StepPattern(
                            patternParts = listOf(
                                TextStepPatternPart(text = "I login as "),
                                ParamStepPatternPart(
                                    name = "username",
                                    typeMeta = StringTypeMeta(),
                                    description = null
                                ),
                                TextStepPatternPart(text = "/"),
                                ParamStepPatternPart(
                                    name = "password",
                                    typeMeta = StringTypeMeta(),
                                    description = null
                                ),
                                TextStepPatternPart(text = " to "),
                                ParamStepPatternPart(
                                    name = "host",
                                    typeMeta = StringTypeMeta(),
                                    description = null
                                ),
                                TextStepPatternPart(text = "/"),
                                ParamStepPatternPart(
                                    name = "port",
                                    typeMeta = NumberTypeMeta("int"),
                                    description = "param description"
                                )
                            )
                        ),
                        description = null,
                        className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                        methodName = "thenStepWithParameters",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[13],
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
                        methodName = "givenAStepInLib2",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )
            assertThat(
                sortedSteps[14],
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
                        methodName = "aStep",
                        resultType = ObjectTypeMeta(javaType = "void", fields = emptyList())
                    )
                )
            )


            val sortedHooks = stepScanResult.hooks.sortedWith(
                compareBy(
                    { it.className },
                    { it.phase },
                    { it.methodName }
                )
            )

            assertThat(sortedHooks, hasSize(13))

            assertThat(
                sortedHooks[0],
                equalTo(
                    HookDef(
                        phase = HookPhase.BEFORE_ALL_TESTS,
                        className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                        methodName = "beforeAllTestsSimple",
                        order = HooksConstants.DEFAULT_ORDER,
                        description = null
                    )
                )
            )
            assertThat(
                sortedHooks[1],
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
                sortedHooks[2],
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
                sortedHooks[3],
                equalTo(
                    HookDef(
                        phase = HookPhase.BEFORE_EACH_TEST,
                        className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                        methodName = "beforeTestSimple",
                        order = HooksConstants.DEFAULT_ORDER,
                        description = null
                    )
                )
            )
            assertThat(
                sortedHooks[4],
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
                sortedHooks[5],
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
                sortedHooks[6],
                equalTo(
                    HookDef(
                        phase = HookPhase.AFTER_EACH_TEST,
                        className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                        methodName = "afterTestSimple",
                        order = HooksConstants.DEFAULT_ORDER,
                        description = null
                    )
                )
            )
            assertThat(
                sortedHooks[7],
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
                sortedHooks[8],
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
                sortedHooks[9],
                equalTo(
                    HookDef(
                        phase = HookPhase.AFTER_ALL_TESTS,
                        className = "com.testerum.scanner_it_steplib1.steps.StepLib1MySteps",
                        methodName = "afterAllTestSimple",
                        order = HooksConstants.DEFAULT_ORDER,
                        description = null
                    )
                )
            )
            assertThat(
                sortedHooks[10],
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
                sortedHooks[11],
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
            assertThat(
                sortedHooks[12],
                equalTo(
                    HookDef(
                        phase = HookPhase.BEFORE_EACH_TEST,
                        className = "com.testerum.scanner_it_steplib3java.steps.StepLib3JavaSteps",
                        methodName = "beforeEachTest",
                        order = HooksConstants.DEFAULT_ORDER,
                        description = null
                    )
                )
            )

            val sortedSettings = stepScanResult.settingDefinitions.sortedBy { it.key }

            assertThat(sortedSettings, hasSize(8))

            assertThat(
                sortedSettings[0],
                equalTo(
                    SettingDefinition(
                        key = "stepLib1.other",
                        label = "1-Other",
                        type = SettingType.TEXT,
                        defaultValue = "yep",
                        description = "other description",
                        category = "other category"
                    )
                )
            )
            assertThat(
                sortedSettings[1],
                equalTo(
                    SettingDefinition(
                        key = "stepLib1.param1",
                        label = "1-Param 1",
                        type = SettingType.NUMBER,
                        defaultValue = "10",
                        description = "param1 description",
                        category = "param1 category"
                    )
                )
            )
            assertThat(
                sortedSettings[2],
                equalTo(
                    SettingDefinition(
                        key = "stepLib1.param2",
                        label = "1-Param 2",
                        type = SettingType.TEXT,
                        defaultValue = "some text",
                        description = "param2 description",
                        category = "param2 category"
                    )
                )
            )
            assertThat(
                sortedSettings[3],
                equalTo(
                    SettingDefinition(
                        key = "stepLib1.param3",
                        label = "1-Param 3",
                        type = SettingType.FILESYSTEM_DIRECTORY,
                        defaultValue = "/some/path",
                        description = "param3 description",
                        category = "param3 category"
                    )
                )
            )
            assertThat(
                sortedSettings[4],
                equalTo(
                    SettingDefinition(
                        key = "stepLib3Java.param1",
                        label = "3-Param 1",
                        type = SettingType.NUMBER,
                        defaultValue = "10",
                        description = "param1 description",
                        category = "param1 category"
                    )
                )
            )
            assertThat(
                sortedSettings[5],
                equalTo(
                    SettingDefinition(
                        key = "stepLib3Java.param2",
                        label = "3-Param 2",
                        type = SettingType.TEXT,
                        defaultValue = "defVal",
                        description = "param2 description",
                        category = "param2 category"
                    )
                )
            )
            assertThat(
                sortedSettings[6],
                equalTo(
                    SettingDefinition(
                        key = "stepLib3Java.param3",
                        label = "3-Param 3",
                        type = SettingType.NUMBER,
                        defaultValue = "13",
                        description = "param3 description",
                        category = "param3 category"
                    )
                )
            )
            assertThat(
                sortedSettings[7],
                equalTo(
                    SettingDefinition(
                        key = "stepLib3Java.param4",
                        label = "3-Param 4",
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

}
