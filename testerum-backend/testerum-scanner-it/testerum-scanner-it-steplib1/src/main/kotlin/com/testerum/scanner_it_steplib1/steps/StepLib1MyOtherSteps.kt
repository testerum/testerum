package com.testerum.scanner_it_steplib1.steps

import com.testerum.api.annotations.settings.annotation.DeclareSetting
import com.testerum.api.test_context.settings.model.SettingType
import com.testerum.api.annotations.steps.Given

@DeclareSetting(key = "stepLib1.other", type = SettingType.TEXT, defaultValue = "yep", description = "other description", category = "other category")
class StepLib1MyOtherSteps {

    @Given(value = "another step")
    fun givenAnotherStep() { }

}