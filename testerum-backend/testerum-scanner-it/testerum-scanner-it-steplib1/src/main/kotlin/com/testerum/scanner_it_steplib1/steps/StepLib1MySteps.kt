@file:Suppress("UNUSED_PARAMETER")

package com.testerum.scanner_it_steplib1.steps

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
import com.testerum.api.test_context.settings.model.SettingType
import java.util.concurrent.TimeUnit

@DeclareSettings([
    (DeclareSetting(key = "stepLib1.param1", type = SettingType.NUMBER, defaultValue = "10", description = "param1 description", category = "param1 category")),
    (DeclareSetting(key = "stepLib1.param2", type = SettingType.TEXT, defaultValue = "some text", description = "param2 description", category = "param2 category")),
    (DeclareSetting(key = "stepLib1.param3", type = SettingType.FILESYSTEM_DIRECTORY, defaultValue = "/some/path", description = "param3 description", category = "param3 category"))
])
class StepLib1MySteps {

    //~~~~~~ given ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Given(value = "a simple step")
    fun givenSimpleStep() { }

    @Given(value = "I login as <<username>>/<<password>> to <<host>>/<<port>>")
    fun givenStepWithParameters(username: String,
                                password: String,
                                host: String,
                                @Param(description = "param description") port: Int) { }

    @Given(value = "a step with all annotation fields", description = "given description")
    fun givenStepWithAllAnnotationFields() { }

    @Given(value = "step with an <<enumParameter>>")
    fun givenAStepWithAnEnumParameter(enumParameter: TimeUnit) { }


    //~~~~~~ when ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @When(value = "a simple step")
    fun whenSimpleStep() { }

    @When(value = "I login as <<username>>/<<password>> to <<host>>/<<port>>")
    fun whenStepWithParameters(username: String,
                               password: String,
                               host: String,
                               @Param(description = "param description") port: Int) { }

    @When(value = "a step with all annotation fields", description = "when description")
    fun whenStepWithAllAnnotationFields() { }

    @When(value = "step with an <<enumParameter>>")
    fun whenAStepWithAnEnumParameter(enumParameter: TimeUnit) { }


    //~~~~~~ then ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Then(value = "a simple step")
    fun thenSimpleStep() { }

    @Then(value = "I login as <<username>>/<<password>> to <<host>>/<<port>>")
    fun thenStepWithParameters(username: String,
                               password: String,
                               host: String,
                               @Param(description = "param description") port: Int) { }

    @Then(value = "a step with all annotation fields", description = "then description")
    fun thenStepWithAllAnnotationFields() { }

    @Then(value = "step with an <<enumParameter>>")
    fun thenAStepWithAnEnumParameter(enumParameter: TimeUnit) { }


    //~~~~~~ hooks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @BeforeEachTest
    fun beforeTestSimple() { }

    @BeforeEachTest(description = "beforeTest description")
    fun beforeTestWithDescription() { }

    @BeforeEachTest(description = "beforeTest with all annotation fields", order = 100)
    fun beforeTestWithAllAnnotationFields() { }

    @BeforeAllTests
    fun beforeAllTestsSimple() { }

    @BeforeAllTests(description = "beforeAllTests description")
    fun beforeAllTestsWithDescription() { }

    @BeforeAllTests(description = "beforeAllTests with all annotation fields", order = 100)
    fun beforeAllTestsWithAllAnnotationFields() { }

    @AfterEachTest
    fun afterTestSimple() { }

    @AfterEachTest(description = "afterTest description")
    fun afterTestWithDescription() { }

    @AfterEachTest(description = "afterTest with all annotation fields", order = 100)
    fun afterTestWithAllAnnotationFields() { }

    @AfterAllTests
    fun afterAllTestSimple() { }

    @AfterAllTests(description = "afterAllTest description")
    fun afterAllTestWithDescription() { }

    @AfterAllTests(description = "afterAllTest with all annotation fields", order = 100)
    fun afterAllTestWithAllAnnotationFields() { }

}
